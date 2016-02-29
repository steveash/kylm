package com.github.steveash.kylm.model.immutable;

import java.util.Vector;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.cursors.IntObjectCursor;
import com.github.steveash.kylm.model.LanguageModel;
import com.github.steveash.kylm.model.ngram.BranchNode;
import com.github.steveash.kylm.model.ngram.NgramLM;
import com.github.steveash.kylm.model.ngram.NgramNode;

/**
 * Converts a NGramLM to an immutable version
 * @author Steve Ash
 */
public class ImmutableLMConverter {

    public ImmutableLM convert(NgramLM ngram) {
//        int reserve = 3 + ngram.getUnknownModelCount();

        BranchNode root = ngram.getRoot();
        ImmutableNode newRoot = convertThis(root);
        updateParents(newRoot, null);
        ImmutableSymbolSet symbols = new ImmutableSymbolSet(ngram.getVocab());
        Pattern[] unkPatterns = makeUnkPatterns(ngram.getUnknownModels());
        return new ImmutableLM(
                ngram.getCountTerminals(),
                symbols,
                unkPatterns,
                newRoot
        );
    }

    @Nullable
    private Pattern[] makeUnkPatterns(@Nullable LanguageModel[] unknownModels) {
        if (unknownModels == null) {
            return null;
        }
        int unkSize = unknownModels.length;
        Pattern[] pats = new Pattern[unkSize];
        for (int i = 0; i < unkSize; i++) {
            pats[i] = unknownModels[i].getRegex();
        }
        return pats;
    }

    static void updateParents(ImmutableNode node, ImmutableNode parent) {
        node.parent = parent;
        for (IntObjectCursor<ImmutableNode> child : node) {
            updateParents(child.value, node);
        }
    }

    private ImmutableNode convertThis(NgramNode source) {
        if (source instanceof BranchNode) {
            // need to convert each of the children
            BranchNode bsource = (BranchNode) source;
            Vector<NgramNode> sourceChilds = bsource.getChildrenRaw();
            IntObjectHashMap<ImmutableNode> childs = new IntObjectHashMap<>();
            int childSize = (sourceChilds == null ? 0 : sourceChilds.size());
            for (int i = 0; i < childSize; i++) {
                NgramNode maybeChild = sourceChilds.get(i);
                if (maybeChild == null) {
                    continue;
                }
                ImmutableNode newChild = convertThis(maybeChild);
                childs.put(maybeChild.getId(), newChild);
            }
            return new ImmutableNode(
                    source.getId(),
                    source.getScore(),
                    source.getBackoffScore(),
                    childs
            );
        } else {
            // leaf node so no more recursion to do
            return new ImmutableNode(
                    source.getId(),
                    source.getScore(),
                    source.getBackoffScore(),
                    null // leaf nodes have null children
            );
        }
    }
}
