package com.github.steveash.kylm.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * @author Steve Ash
 */
public class VarIntSerializerTest {
    private static final Logger log = LoggerFactory.getLogger(VarIntSerializerTest.class);
    private static final int CHUNK = 1024 * 16;

    @Test
    public void shouldSerializeOne() throws Exception {
        ByteArrayDataOutput bado = ByteStreams.newDataOutput();
        VarIntSerializer.write(337, bado);
        byte[] bytes = bado.toByteArray();
        Assert.assertEquals(2, bytes.length);
        ByteArrayDataInput badi = ByteStreams.newDataInput(bytes);
        Assert.assertEquals(337, VarIntSerializer.read(badi));
    }

    @Test
    public void shouldSerializeNumbers() throws Exception {
        testRange(0, 1_000_000);
        testRange(8_000_000, 10_000_000);
        testRange(16_777_210, 16_800_000);
        testRange(67_107_864, 67_109_864);
        testRange(134_217_700, 135_017_727);
        testRange(Integer.MAX_VALUE - 1_000_000, Integer.MAX_VALUE);

        Random rand = new Random(0xCAFECAFE);
        for (int i = 0; i < 50; i++) {
            int start = Math.abs(rand.nextInt() + 1_000_000);
            testRange(start, start + 1_000_000);
        }
        log.info("Checked lots of ranges");
    }

    private void testRange(long from, long to) throws IOException {
        log.info("testing range " + from + " to " + to);
        long i = from;
        while (i < to) {
            ByteArrayDataOutput bado = ByteStreams.newDataOutput();
            int base = (int)i;
            for (int j = 0; j < CHUNK; j++) {
                VarIntSerializer.write(base + j, bado);
            }
            ByteArrayDataInput badi = ByteStreams.newDataInput(bado.toByteArray());
            for (int j = 0; j < CHUNK; j++) {
                Assert.assertEquals(base + j, VarIntSerializer.read(badi));
            }
            i += CHUNK;
        }
    }
}