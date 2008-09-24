package org.joe_e.testlib;

import java.util.Iterator;

public class DisabledIterable<E> implements Iterable<E> {

    public Iterator<E> iterator() {
        return null;
    }
}
