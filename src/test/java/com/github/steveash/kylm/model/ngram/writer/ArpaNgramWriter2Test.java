package com.github.steveash.kylm.model.ngram.writer;

import java.io.File;

import org.junit.Test;

import com.github.steveash.kylm.model.ngram.NgramLM;
import com.github.steveash.kylm.model.ngram.smoother.KNSmoother;
import com.github.steveash.kylm.reader.Filters;
import com.github.steveash.kylm.reader.TextFileSentenceReader;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * @author Steve Ash
 */
public class ArpaNgramWriter2Test {

    @Test
    public void shouldWriteSmokeTest() throws Exception {
        NgramLM lm = new NgramLM(8, new KNSmoother());
        lm.trainModel(Filters.standard(new TextFileSentenceReader("src/test/resources/lines1.txt")));
        File temp1 = File.createTempFile("lmarpa-orig-", ".txt");
        File temp2 = File.createTempFile("lmarpa-new-", ".txt");
        new ArpaNgramWriter2().write(lm, Files.asCharSink(temp2, Charsets.UTF_8));
        new ArpaNgramWriter().write(lm, temp1.getAbsolutePath());
        System.out.println("Wrote new to " + temp2.getAbsolutePath());
        System.out.println("Wrote old to " + temp1.getAbsolutePath());
        // comment this out to inspect files manually
        temp1.deleteOnExit();
        temp2.deleteOnExit();
    }
}