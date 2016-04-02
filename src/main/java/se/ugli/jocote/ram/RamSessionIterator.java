package se.ugli.jocote.ram;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.support.SessionIterator;

public class RamSessionIterator implements SessionIterator {

    private Queue<Message> backoutQueue = new ConcurrentLinkedQueue<>();
    private boolean closable;
    private final Queue<Message> connectionQueue;
    private int index = 0;

    public RamSessionIterator(final Queue<Message> connectionQueue) {
        this.connectionQueue = connectionQueue;
    }

    @Override
    public Optional<Message> next() {
        final Message message = connectionQueue.poll();
        if (message != null) {
            backoutQueue.offer(message);
            index++;
            return Optional.of(message);
        }
        return Optional.empty();
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

    @Override
    public int index() {
        return index;
    }

}