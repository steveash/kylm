package com.github.steveash.kylm.reader;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Steve Ash
 */
public class FiltersTest {

    @Test
    public void shouldStandardizeWithPunc() throws Exception {
        Assert.assertArrayEquals(new String[] {"steve", "was", ",", "here", "!"},
                Filters.standardSentence("Steve~~ ", "was,", "HERE!"));

    }
}