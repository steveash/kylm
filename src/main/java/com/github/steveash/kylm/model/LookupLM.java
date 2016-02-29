package com.github.steveash.kylm.model;

import java.util.List;

/**
 * Interface for the basics of a language model that are needed post-training
 * @author Steve Ash
 */
public interface LookupLM {

    double sentenceProbNormalized(List<String> sentence);

    double sentenceProb(List<String> sentence);
}
