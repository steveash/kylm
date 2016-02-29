package com.github.steveash.kylm.model.immutable;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.steveash.kylm.model.ngram.NgramLM;
import com.github.steveash.kylm.model.ngram.smoother.KNSmoother;
import com.github.steveash.kylm.reader.Filters;
import com.github.steveash.kylm.reader.TextFileSentenceReader;

/**
 * @author Steve Ash
 */
public class ImmutableLMTest {
    private static final Logger log = LoggerFactory.getLogger(ImmutableLMTest.class);

    @Test
    public void shouldSerializeAndDeserialize() throws Exception {

        ImmutableLM lm2 = buildLm();

        String[] c = {"scarred", "zippy", "puppies", "whatevz"};
        double c1 = lm2.sentenceProbNormalized(Arrays.asList(c));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(lm2);
        byte[] bytes = baos.toByteArray();
        log.info("Wrote immutable lm in " + bytes.length);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        ImmutableLM lm3 = (ImmutableLM) ois.readObject();

        double c2 = lm3.sentenceProbNormalized(Arrays.asList(c));
        log.info("Got " + c1 + " vs " + c2);
        assertEquals(c1, c2, 0.0001);
    }

    private ImmutableLM buildLm() throws Exception {
        NgramLM lm1 = new NgramLM(8, new KNSmoother());
        lm1.trainModel(Filters.standard(new TextFileSentenceReader("src/test/resources/lines1.txt")));
        return new ImmutableLMConverter().convert(lm1);
    }
}