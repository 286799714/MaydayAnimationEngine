package com.maydaymemory.mae.util;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class DirtyTrackingArrayList<T> implements List<T>, RandomAccess{
    protected final ArrayList<T> innerList;
    private boolean dirty = false;

    public DirtyTrackingArrayList(@Nonnull ArrayList<T> initialList) {
        innerList = initialList;
    }

    /**
     * Performs the actual refresh logic specific to the subclass.
     * This method is called by {@link #refresh()} to update the internal state.
     *
     * @return {@code true} if refreshing is finished, {@code false} otherwise.
     */
    protected abstract boolean onRefresh();

    /**
     * Triggers a refresh of the internal state if the object is marked as dirty.
     * The result of {@link #onRefresh()} determines whether the dirty flag is updated.
     */
    public void refresh() {
        if (dirty) {
            dirty = !this.onRefresh();
        }
    }

    /**
     * Returns whether the internal state has been marked as dirty
     * and requires a refresh.
     *
     * @return {@code true} if the state is dirty, {@code false} otherwise.
     */
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public int size() {
        return innerList.size();
    }

    @Override
    public boolean isEmpty() {
        return innerList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return innerList.contains(o);
    }

    @Override
    @Nonnull
    public Iterator<T> iterator() {
        return innerList.iterator();
    }

    @Override
    @Nonnull
    public Object[] toArray() {
        return innerList.toArray();
    }

    @Override
    @Nonnull
    public <U> U[] toArray(@Nonnull U[] array) {
        return innerList.toArray(array);
    }

    @Override
    public boolean add(T item) {
        dirty = true;
        return innerList.add(item);
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = innerList.remove(o);
        dirty = dirty || removed;
        return removed;
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> collection) {
        return innerList.containsAll(collection);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends T> collection) {
        boolean added = innerList.addAll(collection);
        dirty = dirty || added;
        return added;
    }

    @Override
    public boolean addAll(int i, @Nonnull Collection<? extends T> collection) {
        boolean added = innerList.addAll(i, collection);
        dirty = dirty || added;
        return added;
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> collection) {
        boolean removed = innerList.removeAll(collection);
        dirty = dirty || removed;
        return removed;
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> collection) {
        boolean retained = innerList.retainAll(collection);
        dirty = dirty || retained;
        return retained;
    }

    @Override
    public void clear() {
        innerList.clear();
        dirty = false;
    }

    @Override
    public T get(int i) {
        return innerList.get(i);
    }

    @Override
    public T set(int i, T keyframe) {
        T oldOne = innerList.set(i, keyframe);
        if (!Objects.equals(oldOne, keyframe)) {
            dirty = true;
        }
        return oldOne;
    }

    @Override
    public void add(int i, T item) {
        dirty = true;
        innerList.add(i, item);
    }

    @Override
    public T remove(int i) {
        dirty = true;
        return innerList.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return innerList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return innerList.lastIndexOf(o);
    }

    @Override
    public @Nonnull ListIterator<T> listIterator() {
        return innerList.listIterator();
    }

    @Override
    public @Nonnull ListIterator<T> listIterator(int i) {
        return innerList.listIterator(i);
    }

    @Override
    public @Nonnull List<T> subList(int i, int i1) {
        return innerList.subList(i, i1);
    }
}
