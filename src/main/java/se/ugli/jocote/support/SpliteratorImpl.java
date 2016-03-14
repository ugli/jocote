package se.ugli.jocote.support;

import static java.util.Spliterators.spliterator;

import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;

import se.ugli.jocote.Iterator;
import se.ugli.jocote.Message;

class SpliteratorImpl implements Spliterator<Message> {

    private final Iterator<Message> iterator;
    private final int batchSize;

    SpliteratorImpl(final Iterator<Message> iterator, final int batchSize) {
        this.iterator = iterator;
        this.batchSize = batchSize;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super Message> action) {
        if (action == null)
            throw new NullPointerException();
        final Optional<Message> next = iterator.next();
        if (next.isPresent()) {
            action.accept(next.get());
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<Message> trySplit() {
        final Message[] array = new Message[batchSize];
        int toIndex = 0;
        Optional<Message> next = iterator.next();
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
