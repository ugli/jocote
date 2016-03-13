package se.ugli.jocote.support;

import static java.util.Spliterators.spliterator;

import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;

import se.ugli.jocote.Iterator;

class SpliteratorImpl implements Spliterator<byte[]> {

    private final Iterator<byte[]> iterator;
    private final int batchSize;

    SpliteratorImpl(final Iterator<byte[]> iterator, final int batchSize) {
        this.iterator = iterator;
        this.batchSize = batchSize;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super byte[]> action) {
        if (action == null)
            throw new NullPointerException();
        final Optional<byte[]> next = iterator.next();
        if (next.isPresent()) {
            action.accept(next.get());
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<byte[]> trySplit() {
        final byte[][] array = new byte[batchSize][];
        int toIndex = 0;
        Optional<byte[]> next = iterator.next();
        while (next.isPresent()) {
            array[toIndex++] = next.get();
            next = iterator.next();
        }
        if (toIndex > 0)
            return spliterator(array, 0, toIndex, characteristics());
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return 0;
    }

}
