package com.github.steveash.kylm.model.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.carrotsearch.hppc.ObjectIntHashMap;
import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.cursors.ObjectIntCursor;
import com.github.steveash.kylm.util.SymbolSet;
import com.github.steveash.kylm.util.VarIntSerializer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Runtime symbol set that is immutable
 * @author Steve Ash
 */
public class ImmutableSymbolSet implements Serializable {

    private final ObjectIntMap<String> wordtoId;
    private final List<String> idToWord;

    public ImmutableSymbolSet(SymbolSet symbolSet) {

        ObjectIntHashMap<String> map = new ObjectIntHashMap<>(symbolSet.ids.size());
        for (Entry<String, Integer> entry : symbolSet.ids.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }

        idToWord = Lists.newArrayList(symbolSet.syms);
        wordtoId = map;
    }

    public ImmutableSymbolSet(ObjectIntMap<String> wordtoId, List<String> idToWord) {
        this.wordtoId = wordtoId;
        this.idToWord = idToWord;
    }

    @Nullable
    public Integer lookupWord(String word) {
        int maybe = wordtoId.getOrDefault(word, -1);
        if (maybe == -1) {
            return null;
        }
        return maybe;
    }

    public String lookupId(int id) {
        return checkNotNull(idToWord.get(id));
    }

    private Object writeReplace() {
        return new ImmutableSymbolSetProxy(this);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    static class ImmutableSymbolSetProxy implements Serializable, Externalizable {
        private static final long serialVersionUID = 2954684124928501878L;

        private HashMap<String, Integer> wordtoId;
        private List<String> idToWord;

        public ImmutableSymbolSetProxy(ImmutableSymbolSet iss) {
            this.wordtoId = Maps.newHashMap();
            for (ObjectIntCursor<String> entry : iss.wordtoId) {
                wordtoId.put(entry.key, entry.value);
            }
            this.idToWord = iss.idToWord;
        }

        public ImmutableSymbolSetProxy() {
        }

        private Object readResolve() throws ObjectStreamException {
            ObjectIntHashMap<String> map = new ObjectIntHashMap<>(wordtoId.size());
            for (Entry<String, Integer> entry : wordtoId.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            return new ImmutableSymbolSet(map, this.idToWord);
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            VarIntSerializer.write(this.idToWord.size(), out);
            // we need to write out all of the exceptions to the "normal" mapping (in idToWord -> index)
            // but we first need to count to know how many to write out
            int count = 0;
            for (Entry<String, Integer> entry : this.wordtoId.entrySet()) {
                if (!entry.getKey().equals(this.idToWord.get(entry.getValue()))) {
                    count += 1;
                }
            }
            VarIntSerializer.write(count, out);

            for (String s : this.idToWord) {
                out.writeUTF(s);
            }

            for (Entry<String, Integer> entry : this.wordtoId.entrySet()) {
                if (!entry.getKey().equals(this.idToWord.get(entry.getValue()))) {
                    out.writeUTF(entry.getKey());
                    VarIntSerializer.write(entry.getValue(), out);
                    count -= 1;
                }
            }
            if (count != 0) {
                throw new IllegalStateException("somehow the number of exception mappings changed, " +
                        "can't serialize while mutating");
            }
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

            int listCount = VarIntSerializer.read(in);
            int excCount = VarIntSerializer.read(in);
            this.wordtoId = Maps.newHashMapWithExpectedSize(listCount + excCount);
            this.idToWord = Lists.newArrayListWithCapacity(listCount);

            for (int i = 0; i < listCount; i++) {
                String value = in.readUTF();
                this.idToWord.add(value);
                this.wordtoId.put(value, i);
            }

            // there are some exceptions that we might need to add to the list
            for (int i = 0; i < excCount; i++) {
                String key = in.readUTF();
                int val = VarIntSerializer.read(in);
                this.wordtoId.put(key, val);
            }
        }
    }
}
