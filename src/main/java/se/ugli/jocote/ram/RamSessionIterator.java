package se.ugli.jocote.ram;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.SessionIterator;

public class RamSessionIterator<T> implements SessionIterator<T> {

    private Queue<Message> backoutQueue = new ConcurrentLinkedQueue<Message>();
    private boolean closable;
    private final Queue<Message> connectionQueue;
    private final Consumer<T> consumer;

    public RamSessionIterator(final Queue<Message> connectionQueue, final Consumer<T> consumer) {
        this.connectionQueue = connectionQueue;
        this.consumer = consumer;
    }

    @Override
    public Optional<T> next() {
        final Message message = connectionQueue.poll();
        if (message != null) {
            backoutQueue.offer(message);
            return consumer.receive(message.body, new RamMessageContext(message));
        }
        return Optional.empty();
    }

    @Override
    public void acknowledgeMessages() {
        closable = true;
    }

    @Override
    public void close() {
        if (!closable)
            throw new JocoteException("You have to acknowledge or leave messages before closing");
        backoutQueue = null;
    }

    @Override
    public void leaveMessages() {
        for (final Message message : backoutQueue)
            connectionQueue.offer(message);
        closable = true;
    }

}