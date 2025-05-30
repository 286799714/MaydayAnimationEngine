package com.maydaymemory.mae.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Iterables {
    public static <T> Iterable<T> concat(final Iterable<? extends T> a, final Iterable<? extends T> b) {
        return () -> new Iterator<T>() {
            final Iterator<? extends T> current = a.iterator();
            Iterator<? extends T> next = null;

            @Override
            public boolean hasNext() {
                if (current.hasNext()) {
                    return true;
                }
                if (next == null) {
                    next = b.iterator();
                }
                return next.hasNext();
            }

            @Override
            public T next() {
                if (hasNext()) {
                    return current.hasNext() ? current.next() : next.next();
                }
                throw new NoSuchElementException();
            }
        };
    }
}
