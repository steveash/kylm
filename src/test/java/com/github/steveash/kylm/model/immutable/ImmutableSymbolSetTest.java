package com.github.steveash.kylm.model.immutable;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ObjectIntHashMap;
import com.google.common.collect.Lists;

/**
 * @author Steve Ash
 */
public class ImmutableSymbolSetTest {
    private static final Logger log = LoggerFactory.getLogger(ImmutableSymbolSetTest.class);

    @Test
    public void shouldSerialize() throws Exception {

        ObjectIntHashMap<String> map = new ObjectIntHashMap<>();
        map.put("steve", 0);
        map.put("ash", 1);
        map.put("bob", 2);

        ImmutableSymbolSet iss = new ImmutableSymbolSet(map, Lists.newArrayList("steve", "ash", "bob"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(iss);
        byte[] written = bos.toByteArray();
        log.info("Wrote " + written.length + " bytes serialized");
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(written));
        ImmutableSymbolSet iss2 = (ImmutableSymbolSet) ois.readObject();
        assertEquals(0, (int)iss2.lookupWord("steve"));
        assertEquals(1, (int)iss2.lookupWord("ash"));
        assertEquals(2, (int)iss2.lookupWord("bob"));
    }
}