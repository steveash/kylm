package com.github.steveash.kylm.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import com.github.steveash.kylm.util.SymbolSet;
import com.google.common.collect.Lists;

public class ClassMap implements Serializable {

    private static final long serialVersionUID = 1707818585575921117L;

    public SymbolSet classes = null;
    public List<Integer> idMap = null;
    public List<Float> probMap = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassMap classMap = (ClassMap) o;

        if (classes != null ? !classes.equals(classMap.classes) : classMap.classes != null) return false;
        if (idMap != null ? !idMap.equals(classMap.idMap) : classMap.idMap != null) return false;
        return probMap != null ? probMap.equals(classMap.probMap) : classMap.probMap == null;
    }

    @Override
    public int hashCode() {
        int result = classes != null ? classes.hashCode() : 0;
        result = 31 * result + (idMap != null ? idMap.hashCode() : 0);
        result = 31 * result + (probMap != null ? probMap.hashCode() : 0);
        return result;
    }

    public ClassMap() {
        classes = new SymbolSet();
        idMap = Lists.newArrayList(); //new int[size];
        probMap = Lists.newArrayList();
    }

    public ClassMap(int size) {
        classes = new SymbolSet();
        idMap = Lists.newArrayListWithCapacity(size); //new int[size];
        probMap = Lists.newArrayListWithCapacity(size);
    }

    public void addEntry(int vid, int cid, float prob) {
        while (vid >= idMap.size()) {
            idMap.add(-1);
            probMap.add(0.0f);
        }
        idMap.set(vid, cid);
        probMap.set(vid, prob);
    }

    public int addClass(String symbol) {
        return classes.addSymbol(symbol);
    }

    public int getWordSize() {
        return idMap.size();
    }

    public int getWordClass(int i) {
        return idMap.get(i);
    }

    public float getWordProb(int i) {
        return probMap.get(i);
    }

    public String getClassSymbol(int wordClass) {
        return classes.getSymbol(wordClass);
    }

    public int getClassSize() {
        return classes.getSize();
    }

    public void setWordProb(int i, float prob) {
        probMap.set(i, prob);
    }

    ///////////////////////////////
    // methods for serialization //
    ///////////////////////////////
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(classes);
        out.writeObject(idMap);
        out.writeObject(probMap);
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

        classes = (SymbolSet) in.readObject();
        Object maybeIds = in.readObject();
        if (maybeIds instanceof Vector) {
            idMap = Lists.newArrayList( (Vector)maybeIds);
        } else {
            idMap = (List<Integer>) maybeIds;
        }

        Object maybeProbMap = in.readObject();
        if (maybeIds instanceof Vector) {
            probMap = Lists.newArrayList( (Vector) maybeIds);
        } else {
            probMap = (List<Float>) maybeProbMap;
        }
    }

    public SymbolSet getClasses() {
        return classes;
    }
}
