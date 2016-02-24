package se.ugli.jocote.ram;

import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;

import se.ugli.jocote.Iterator;
import se.ugli.jocote.Message;

public class RamIterator<T> implements Iterator<T> {

    private final Queue<Message> connectionQueue;
    private final Function<Message, Optional<T>> msgFunc;

    public RamIterator(final Queue<Message> connectionQueue, final Function<Message, Optional<T>> msgFunc) {
        this.connectionQueue = connectionQueue;
        this.msgFunc = msgFunc;
    }

    @Override
    public Optional<T> next() {
        final Message message = connectionQueue.poll();
        if (message != null)
            return msgFunc.apply(message);
        return Optional.empty();
    }

}
