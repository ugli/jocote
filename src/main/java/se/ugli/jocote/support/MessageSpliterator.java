package se.ugli.jocote.support;

import java.util.Optional;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;

import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;

public class MessageSpliterator extends AbstractSpliterator<Message> {

    private static final int CHARACTERISTICS = ORDERED | CONCURRENT | NONNULL | IMMUTABLE;
    private final MessageIterator iterator;

    protected MessageSpliterator(final MessageIterator iterator) {
        super(Long.MAX_VALUE, CHARACTERISTICS);
        this.iterator = iterator;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super Message> action) {
        final Optional<Message> next = iterator.next();
        if (next.isPresent()) {
            action.accept(next.get());
            return true;
        }
        return false;
    }

}