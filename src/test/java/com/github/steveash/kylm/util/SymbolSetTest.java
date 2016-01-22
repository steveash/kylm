package com.github.steveash.kylm.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Steve Ash
 */
public class SymbolSetTest {
    private static final Logger log = LoggerFactory.getLogger(SymbolSetTest.class);

    @Test
    public void shouldReadWriteValues() throws Exception {
        SymbolSet set = new SymbolSet();
        int id1 = set.addSymbol("S");
        int id2 = set.addSymbol("S");
        assertEquals(id1, id2);

        int id3 = set.addSymbol("T");
        assertTrue(id3 != id2);

        log.info("Got id1,2 = " + id1);
        log.info("Got id3 = " + id3);
    }
}