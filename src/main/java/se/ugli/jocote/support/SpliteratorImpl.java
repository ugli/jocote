package se.ugli.jocote.support;

import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;

import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;

import static java.util.Spliterators.spliterator;

class SpliteratorImpl implements Spliterator<Message> {

    private static final int BATCH_UNIT = 1 << 10;
    private final MessageIterator iterator;

    SpliteratorImpl(final MessageIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super Message> action) {
        Objects.requireNonNull(action);
        final Optional<Message> next = iterator.next();
        if (next.isPresent()) {
            action.accept(next.get());
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<Message> trySplit() {
        final Message[] array = new Message[BATCH_UNIT];
        int toIndex = 0;
        Optional<Message> next = iterator.next();
        if (next.isPresent()) {
            while (next.isPresent()) {
                array[toIndex++] = next.get();
                next = iterator.next();
            }
            return spliterator(array, 0, toIndex, 0);
        }
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