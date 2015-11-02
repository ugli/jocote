package se.ugli.jocote.ram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Consumer;
import se.ugli.jocote.Iterator;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.SessionConsumer;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.SimpleConsumer;

class RamConnection implements Connection {

    private final Queue<Message> queue = new ConcurrentLinkedQueue<Message>();
    private final List<Consumer<?>> subscribers = new ArrayList<Consumer<?>>();

    @Override
    public void close() {
        if (subscribers.isEmpty())
            queue.clear();
    }

    @Override
    public <T> T get() {
        return get(new SimpleConsumer<T>());
    }

    @Override
    public <T> T get(final Consumer<T> consumer) {
        final Message message = queue.poll();
        if (message != null)
            return consumer.receive(message.body, new RamMessageContext(message));
        return null;
    }

    @Override
    public <T> T get(final SessionConsumer<T> consumer) {
        final Message message = queue.poll();
        if (message != null) {
            final RamSessionMessageContext cxt = new RamSessionMessageContext(message, queue);
            final T result = consumer.receive(message.body, cxt);
            if (cxt.isClosable())
                return result;
            throw new JocoteException("You have to acknowledge or leave message");
        }
        return null;
    }

    @Override
    public <T> Iterator<T> iterator() {
        return iterator(new SimpleConsumer<T>());
    }

    @Override
    public <T> Iterator<T> iterator(final Consumer<T> consumer) {
        return new RamIterator<T>(queue, consumer);
    }

    @Override
    public void put(final Object message) {
        put(message, new HashMap<String, Object>(), new HashMap<String, Object>());
    }

    @Override
    public void put(final Object body, final Map<String, Object> headers, final Map<String, Object> properties) {
        if (subscribers.isEmpty())
            queue.offer(new Message(body, headers, properties));
        else
            for (final Consumer<?> consumer : subscribers)
                consumer.receive(body, new RamMessageContext(headers, properties));
    }

    @Override
    public <T> SessionIterator<T> sessionIterator() {
        return sessionIterator(new SimpleConsumer<T>());
    }

    @Override
    public <T> SessionIterator<T> sessionIterator(final Consumer<T> consumer) {
        return new RamSessionIterator<T>(queue, consumer);
    }

    <T> Subscription<T> addSubscrition(final Consumer<T> consumer) {
        for (final Message message : queue)
            consumer.receive(message.body, new RamMessageContext(message));
        subscribers.add(consumer);
        return new Subscription<T>() {

            @Override
            public void close() {
                subscribers.remove(consumer);
                RamConnection.this.close();
            }
        };
    }

}