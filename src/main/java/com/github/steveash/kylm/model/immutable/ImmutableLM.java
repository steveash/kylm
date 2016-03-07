package com.github.steveash.kylm.model.immutable;

import java.io.Externalizable;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.github.steveash.kylm.model.LookupLM;

/**
 * An immutable model that is just useful for post-training runtime evaluation.
 * @author Steve Ash
 */
@ThreadSafe
public class ImmutableLM implements Serializable, LookupLM {

    final boolean countTerminals; // true
    final ImmutableSymbolSet symbols;
    @Nullable final Pattern[] ukModelPatterns; // get patterns from parent LM
    final ImmutableNode root;

    public ImmutableLM(boolean countTerminals,
            ImmutableSymbolSet symbols,
            @Nullable Pattern[] ukModelPatterns,
            ImmutableNode root
    ) {
        this.countTerminals = countTerminals;
        this.symbols = symbols;
        this.ukModelPatterns = ukModelPatterns;
        this.root = root;
    }

    /**
     * Get the log10-likelihood of a sentence normalized with length.
     * This value in fact equals to the perplexity of a sentence
     * @param sentence
     * @return PP(sent)
     */
    @Override
    public double sentenceProbNormalized(List<String> sentence) {
        return sentenceProb(sentence) / -(sentence.size() + 2);
    }

    @Override
    public double sentenceProb(List<String> sentence) {
        double prob = 0.0f;
        // get the sentence IDs
        int[] iids = wordsToWordIds(sentence);

        // check to make sure that nodes exist for every id
        for (int i = 0; i < iids.length; i++)
            if (root.getChild(iids[i]) == null)
                iids[i] = this.findUnknownId(symbols.lookupId(iids[i]));

        int idx;

        // start with the terminal symbol as the context
        ImmutableNode context = root.getChild(0), child;

        for (int i = 0; i < iids.length - 1; i++) {
            idx = iids[i + 1];
            // first, fall back to a node that has children
            while (!context.hasChildren()) {
                context = context.getFallback();
            }
            // then, fall back to a node that actually can predict the word
            while ((child = context.getChild(idx)) == null) {
                // add the fallback penalty
                prob += context.getBackoffScore();
                ImmutableNode newContext = context.getFallback();
                if (newContext == null) {
                    throw new IllegalArgumentException("Could not find word " +
                            idx + " - " + this.symbols.lookupId(idx) + " in unigram vocabulary while " +
                            "processing " + sentence + " at " + i);
                }
                context = newContext;
            }
            // add the score
            prob += child.getScore();
            context = child;
        }

        return prob;
    }

    private int[] wordsToWordIds(List<String> sentence) {
        int size = sentence.size();
        int[] ret = new int[size + (countTerminals ? 2 : 1)];
        for (int i = 0; i < size; i++)
            ret[i + 1] = wordToWordId(sentence.get(i));
        return ret;
    }

    private int wordToWordId(String word) {
        Integer idx = this.symbols.lookupWord(word);
        if (idx == null) {
            idx = findUnknownId(word);
        }
        return idx;
    }

    private Integer findUnknownId(String word) {
        if (ukModelPatterns == null)
            return 2;
        for (int i = 0; i < ukModelPatterns.length; i++) {
            Pattern p = ukModelPatterns[i];
            if (p == null || p.matcher(word).matches())
                return i + 2;
        }
        throw new IllegalArgumentException("No unknown word model found to match " + word);
    }

    private Object writeReplace() {
        return new ImmutableLmProxy(this);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }
}
