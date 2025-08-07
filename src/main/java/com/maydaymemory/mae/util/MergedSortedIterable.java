package com.maydaymemory.mae.util;
import javax.annotation.Nonnull;
import java.util.*;

/**
 * Merged Sorted Iterable, for merging multiple sorted iterables into a single sorted iterable.
 * 
 * <p>This class provides functionality to merge multiple sorted collections into one sorted collection
 * while maintaining the sorted order. It uses a priority queue (min-heap) to efficiently merge
 * the elements from multiple iterables in sorted order.</p>
 * 
 * <p>The merging process works as follows:</p>
 * <ul>
 *   <li>Each input iterable must be sorted according to the provided comparator</li>
 *   <li>The merged result maintains the same sorting order</li>
 *   <li>Uses a priority queue to efficiently select the next smallest element</li>
 *   <li>Supports lazy evaluation - elements are only processed when iterated</li>
 * </ul>
 * 
 * <p>This is particularly useful in animation systems where multiple sorted keyframe channels
 * need to be merged into a single sorted timeline.</p>
 * 
 * @param <T> The type of elements to be merged
 * 
 * @author MaydayMemory
 * @since 1.0.4
 */
public class MergedSortedIterable<T> implements Iterable<T> {
    /** Collection of sorted iterables to be merged */
    private final Iterable<Iterable<T>> containers;
    
    /** Comparator used for sorting and merging elements */
    private final Comparator<? super T> comparator;

    /**
     * Constructs a new merged sorted iterable.
     * 
     * @param sortedContainers Collection of sorted iterables to merge
     * @param comparator Comparator used for sorting and merging
     */
    public MergedSortedIterable(Iterable<Iterable<T>> sortedContainers, Comparator<? super T> comparator) {
        this.containers = sortedContainers;
        this.comparator = comparator;
    }

    /**
     * Returns an iterator that provides elements in sorted order.
     * 
     * <p>The iterator merges elements from all input iterables while maintaining
     * the sorted order defined by the comparator.</p>
     * 
     * @return Iterator that provides merged elements in sorted order
     */
    @Override
    @Nonnull
    public Iterator<T> iterator() {
        return new MergedIterator();
    }

    /**
     * Iterator implementation that merges sorted iterables using a priority queue.
     * 
     * <p>This iterator uses a min-heap to efficiently select the next smallest element
     * from all input iterables, ensuring the merged result maintains sorted order.</p>
     */
    private class MergedIterator implements Iterator<T> {
        /** Priority queue used for efficient element selection */
        private final PriorityQueue<Entry> heap;

        /**
         * Constructs a new merged iterator.
         * 
         * <p>Initializes the priority queue with the first element from each input iterable,
         * creating entries that contain both the element value and its source iterator.</p>
         */
        public MergedIterator() {
            heap = new PriorityQueue<>(Comparator.comparing(entry -> entry.value, comparator));
            for (Iterable<T> container : containers) {
                Iterator<T> it = container.iterator();
                if (it.hasNext()) {
                    T val = it.next();
                    heap.add(new Entry(val, it));
                }
            }
        }

        /**
         * Checks if there are more elements to iterate.
         * 
         * @return true if there are more elements, false otherwise
         */
        @Override
        public boolean hasNext() {
            return !heap.isEmpty();
        }

        /**
         * Returns the next element in sorted order.
         * 
         * <p>Removes the smallest element from the priority queue and adds the next element
         * from its source iterator if available.</p>
         * 
         * @return The next element in sorted order
         * @throws NoSuchElementException if there are no more elements
         */
        @Override
        public T next() {
            if (heap.isEmpty()) throw new NoSuchElementException();
            Entry entry = heap.poll();
            T result = entry.value;
            if (entry.iterator.hasNext()) {
                heap.add(new Entry(entry.iterator.next(), entry.iterator));
            }
            return result;
        }

        /**
         * Entry class that holds both an element value and its source iterator.
         * 
         * <p>This class is used by the priority queue to track which iterator
         * each element came from, allowing efficient retrieval of the next element
         * from the same source.</p>
         */
        private class Entry {
            /** The element value */
            final T value;
            
            /** The source iterator for this element */
            final Iterator<T> iterator;

            /**
             * Constructs a new entry.
             * 
             * @param value The element value
             * @param iterator The source iterator for this element
             */
            Entry(T value, Iterator<T> iterator) {
                this.value = value;
                this.iterator = iterator;
            }
        }
    }
}

