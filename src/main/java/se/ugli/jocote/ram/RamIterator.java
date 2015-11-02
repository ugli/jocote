package se.ugli.jocote.ram;

import java.util.Queue;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.Iterator;

public class RamIterator<T> implements Iterator<T> {

    private final Queue<Message> connectionQueue;
    private final Consumer<T> consumer;

    public RamIterator(final Queue<Message> connectionQueue, final Consumer<T> consumer) {
        this.connectionQueue = connectionQueue;
        this.consumer = consumer;
    }

    @Override
    public T next() {
        final Message message = connectionQueue.poll();
        if (message != null)
            return consumer.receive(message.body, new RamMessageContext(message));
        return null;
    }

}
