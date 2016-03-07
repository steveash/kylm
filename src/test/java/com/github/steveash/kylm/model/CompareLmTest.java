package com.github.steveash.kylm.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.steveash.kylm.model.immutable.ImmutableLM;
import com.github.steveash.kylm.model.immutable.ImmutableLMConverter;
import com.github.steveash.kylm.model.ngram.NgramLM;
import com.github.steveash.kylm.model.ngram.smoother.KNSmoother;
import com.github.steveash.kylm.reader.Filters;
import com.github.steveash.kylm.reader.TextFileSentenceReader;

/**
 * @author Steve Ash
 */
public class CompareLmTest {
    private static final Logger log = LoggerFactory.getLogger(CompareLmTest.class);

    @Test
    public void shouldGenerateSameScores() throws Exception {
        NgramLM lm1 = new NgramLM(8, new KNSmoother());
        lm1.trainModel(Filters.standard(new TextFileSentenceReader("src/test/resources/lines1.txt")));
        String[] a = {"the", "readers", "feel", "good"};
        String[] b = {"care", "about", "friends", "and", "family", "wasting", "their", "time"};
        String[] c = {"scarred", "zippy", "puppies", "whatevz"};
        double a1 = lm1.getSentenceProbNormalized(a);
        double b1 = lm1.getSentenceProbNormalized(b);
        double c1 = lm1.getSentenceProbNormalized(c);
        log.info("Got " + a1 + " ; " + b1 + " ; " + c1);

        ImmutableLM lm2 = new ImmutableLMConverter().convert(lm1);
        double a2 = lm2.sentenceProbNormalized(Arrays.asList(a));
        double b2 = lm2.sentenceProbNormalized(Arrays.asList(b));
        double c2 = lm2.sentenceProbNormalized(Arrays.asList(c));
        log.info("Got " + a2 + " ; " + b2 + " ; " + c2);

        Assert.assertEquals(a1, a2, 0.001);
        Assert.assertEquals(b1, b2, 0.001);
        Assert.assertEquals(c1, c2, 0.001);
    }

    @Test
    public void shouldWorkWithUnkGrams() throws Exception {
        NgramLM lm = new NgramLM(3, new KNSmoother());
        List<String[]> strings = Arrays.asList(
                new String[]{"s", "t", "e", "v", "e"},
                new String[]{"s", "t", "r", "i", "p", "v"},
                new String[]{"r", "i", "p"}
//                new String[]{"x", "i", "p"}

        );
        lm.trainModel(strings);
        ImmutableLM ilm = new ImmutableLMConverter().convert(lm);
        double prob = ilm.sentenceProb(Arrays.asList("s", "t", "e", "v", "e", "n"));
        log.info("Steven got " + prob);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(ilm);
        ImmutableLM ilm2 = (ImmutableLM) new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject();

        double prob2 = ilm2.sentenceProb(Arrays.asList("Z"));
        log.info("StevZn got " + prob2);
    }
}
