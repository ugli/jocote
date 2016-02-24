package se.ugli.jocote.ram;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.SessionIterator;

public class RamSessionIterator<T> implements SessionIterator<T> {

    private Queue<Message> backoutQueue = new ConcurrentLinkedQueue<>();
    private boolean closable;
    private final Queue<Message> connectionQueue;
    private final Function<Message, Optional<T>> msgFunc;

    public RamSessionIterator(final Queue<Message> connectionQueue, final Function<Message, Optional<T>> msgFunc) {
        this.connectionQueue = connectionQueue;
        this.msgFunc = msgFunc;
    }

    @Override
    public Optional<T> next() {
        final Message message = connectionQueue.poll();
        if (message != null) {
            backoutQueue.offer(message);
            return msgFunc.apply(message);
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