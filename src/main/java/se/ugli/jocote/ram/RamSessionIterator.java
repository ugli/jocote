package se.ugli.jocote.ram;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.SessionIterator;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class RamSessionIterator implements SessionIterator {

    private Queue<Message> backoutQueue = new ConcurrentLinkedQueue<>();
    private boolean closable;
    private final Queue<Message> connectionQueue;

    RamSessionIterator(final Queue<Message> connectionQueue) {
        this.connectionQueue = connectionQueue;
    }

    @Override
    public Optional<Message> next() {
        final Message message = connectionQueue.poll();
        if (message != null)
            backoutQueue.offer(message);
        return Optional.ofNullable(message);
    }

    @Override
    public void ack() {
        closable = true;
    }

    @Override
    public void close() {
        if (!closable)
            throw new JocoteException("You have to acknowledge or leave messages before closing");
        backoutQueue = null;
    }

    @Override
    public void nack() {
        for (final Message message : backoutQueue)
            connectionQueue.offer(message);
        closable = true;
    }

}