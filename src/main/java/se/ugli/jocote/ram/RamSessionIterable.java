package se.ugli.jocote.ram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.SessionIterable;

public class RamSessionIterable<T> implements SessionIterable<T> {

    private Queue<Message> backoutQueue = new ConcurrentLinkedQueue<Message>();
    private boolean closable;
    private final Queue<Message> connectionQueue;
    private Iterator<T> iterator;

    public RamSessionIterable(final int limit, final Consumer<T> consumer, final Queue<Message> queue) {
        this.connectionQueue = queue;
        final List<T> messages = new ArrayList<T>();
        while (!queue.isEmpty() && messages.size() != limit) {
            final Message message = queue.poll();
            messages.add(consumer.receive(message.body, new RamMessageContext(message)));
        }
        this.iterator = messages.iterator();
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
        iterator = null;
    }

    @Override
    public Iterator<T> iterator() {
        return iterator;
    }

    @Override
    public void leaveMessages() {
        for (final Message message : backoutQueue)
            connectionQueue.offer(message);
        closable = true;
    }

}
