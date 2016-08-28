package com.github.steveash.kylm.model.ngram;

import java.util.List;

/**
 * @author Steve Ash
 */
public interface WalkerVisitor {
    void visit(int order, List<String> symbols, float score, float backoffScore, boolean hasChildren, boolean isLastOrder);
}
