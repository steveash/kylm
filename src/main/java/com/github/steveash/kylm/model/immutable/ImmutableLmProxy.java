package com.github.steveash.kylm.model.immutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.cursors.IntObjectCursor;
import com.github.steveash.kylm.util.VarIntSerializer;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Serialization proxy for the immutable Lm
 * @author Steve Ash
 */
class ImmutableLmProxy implements Externalizable {
    private static final long serialVersionUID = -5858711928553467996L;

    private boolean countTerminals;
    private ImmutableNode root;
    private Pattern[] ukModelPatterns;
    private ImmutableSymbolSet symbols;

    public ImmutableLmProxy() {
    }

    public ImmutableLmProxy(ImmutableLM source) {
        this.countTerminals = source.countTerminals;
        this.ukModelPatterns = source.ukModelPatterns;
        this.symbols = source.symbols;
        this.root = source.root;
    }

    private void writeNode(ImmutableNode node, ObjectOutput out) throws IOException {
        VarIntSerializer.write(node.id, out);
        out.writeFloat(node.score);
        out.writeFloat(node.bos);

        if (node.children == null) {
            VarIntSerializer.write(0, out); // 0 no children
            return;
        }
        // we have children so first push the child count
        VarIntSerializer.write(node.children.size(), out);
        for (IntObjectCursor<ImmutableNode> child : node.children) {
            writeNode(child.value, out);
        }
    }

    private ImmutableNode readNode(ObjectInput in) throws IOException {
        int id = VarIntSerializer.read(in);
        float score = in.readFloat();
        float bos = in.readFloat();
        int count = VarIntSerializer.read(in);

        if (count == 0) {
            // this is a leaf node
            return new ImmutableNode(id, score, bos, null);
        }

        IntObjectHashMap<ImmutableNode> childs = new IntObjectHashMap<>(count);
        for (int i = 0; i < count; i++) {
            ImmutableNode child = readNode(in);
            childs.put(child.id, child);
        }
        return new ImmutableNode(id, score, bos, childs);
    }

    private Object readResolve() throws ObjectStreamException {
        return new ImmutableLM(
                this.countTerminals,
                this.symbols,
                this.ukModelPatterns,
                root
        );
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(countTerminals);
        out.writeObject(this.ukModelPatterns);
        out.writeObject(this.symbols);
        writeNode(this.root, out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.countTerminals = in.readBoolean();
        this.ukModelPatterns = (Pattern[]) in.readObject();
        this.symbols = (ImmutableSymbolSet) in.readObject();
        this.root = readNode(in);
        ImmutableLMConverter.updateParents(this.root, null);
    }
}
