package com.github.steveash.kylm.model.ngram;

import com.github.steveash.kylm.util.SymbolSet;
import com.google.common.collect.ImmutableList;

/**
 * Allows you to walk all of the levels of an ngram model (as if you were reading the arpa encoded
 * file)
 * @author Steve Ash
 */
public class NgramWalker {

    public NgramWalker(NgramLM model) {
        this.model = model;
        this.symbols = model.getVocab();
        this.maxOrder = model.getN();
    }

    private final NgramLM model;
    private final SymbolSet symbols;
    private final int maxOrder;

    public void walk(WalkerVisitor visitor) {
        visitUnigrams(visitor);
        for (int i = 2; i <= maxOrder; i++) {
            walkNode(model.getRoot(), i, i, ImmutableList.<String>of(), visitor);
        }
    }

    private void walkNode(NgramNode node, int order, int thisLevel, ImmutableList<String> prevTokens, WalkerVisitor visitor) {
        if (thisLevel == 1) {
            // we've walked to the end of the chain so now visit it
            for (NgramNode child : node) {
                String symbol;
                if (child.getId() == 0) {
                    symbol = model.getTerminalSymbol();
                } else {
                    symbol = symbols.getSymbol(child.getId());
                }
                visitor.visit(order,
                        copyAppend(prevTokens, symbol),
                        child.getScore(),
                        child.getBackoffScore(),
                        child.hasChildren(),
                        order == maxOrder
                );
            }
            return;
        }
        // recurse to keep walking until we hit the max level that we're printing right now
        for (NgramNode child : node) {
            if (!child.hasChildren()) {
                continue;
            }
            String symbol = symbols.getSymbol(child.getId());
            ImmutableList<String> prevs = copyAppend(prevTokens, symbol);
            walkNode(child, order, thisLevel - 1, prevs, visitor);
        }
    }

    private ImmutableList<String> copyAppend(ImmutableList<String> prevTokens, String symbol) {
        return ImmutableList.<String>builder()
                .addAll(prevTokens)
                .add(symbol)
                .build();
    }

    private void visitUnigrams(WalkerVisitor visitor) {
        for (NgramNode rootChild : model.getRoot()) {
            if (rootChild.getId() == 0) {
                // emit the terminals
                visitor.visit(1,
                        ImmutableList.of(model.getStartSymbol()),
                        Float.NEGATIVE_INFINITY,
                        rootChild.hasChildren() ? rootChild.getBackoffScore() : Float.NEGATIVE_INFINITY,
                        rootChild.hasChildren(),
                        1 == maxOrder
                );
                visitor.visit(1,
                        ImmutableList.of(model.getTerminalSymbol()),
                        rootChild.getScore(),
                        Float.NEGATIVE_INFINITY,
                        rootChild.hasChildren(),
                        1 == maxOrder
                );
            } else {
                visitor.visit(1,
                        ImmutableList.of(symbols.getSymbol(rootChild.getId())),
                        rootChild.getScore(),
                        rootChild.getBackoffScore(),
                        rootChild.hasChildren(),
                        1 == maxOrder
                );
            }
        }
    }
}
