package com.github.steveash.kylm.model.immutable;

import java.util.Iterator;

import javax.annotation.Nullable;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.cursors.IntObjectCursor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;

/**
 * immutable variant of the NgramNode (and subclasses). we don't really care whats a leaf and whats an
 * inner node.  Inner nodes just have children. If a node doesn't have any children its child map is null
 * to save space
 * @author Steve Ash
 */
public class ImmutableNode implements Iterable<IntObjectCursor<ImmutableNode>> {

    final int id;
    final float score;
    final float bos;

    @Nullable
    final IntObjectHashMap<ImmutableNode> children;
    @Nullable
    volatile ImmutableNode parent = null;

    public ImmutableNode(int id, float score,
            float bos, @Nullable IntObjectHashMap<ImmutableNode> children) {
        this.id = id;
        this.score = score;
        this.bos = bos;
        this.children = children;
    }

    void setParent(ImmutableNode parent) {
        this.parent = parent;
    }

    @Nullable
    public ImmutableNode getChild(int id) {
        if (children == null) {
            // if leaf node, then we never have any children
            return null;
        }
        return children.getOrDefault(id, null);
    }

    public boolean hasChildren() {
        return children != null;
    }

    @Nullable
    public ImmutableNode getFallback() {
        // if the fallback doesn't exist, find it
        if (parent == null) {
            return null;
        }
        // if this is a unigram, the fallback is the root
        if (parent.parent == null) {
            return parent;
        }
        // otherwise, get the parent's fallback and advance one
        ImmutableNode fallback = parent.getFallback();
        Preconditions.checkNotNull(fallback, "poorly constructed graph, non-null parent has null fallback");
        return fallback.getChild(id);
    }

    public float getBackoffScore() {
        return bos;
    }

    public float getScore() {
        return score;
    }

    @Override
    public Iterator<IntObjectCursor<ImmutableNode>> iterator() {
        if (children == null) {
            return Iterators.emptyIterator();
        }
        return children.iterator();
    }
}
