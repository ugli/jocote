package se.ugli.jocote.linkedqueue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Consumer;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.SimpleConsumer;

class LinkedQueueConnection implements Connection {

    private final Queue<Message> queue = new ConcurrentLinkedQueue<Message>();
    private final List<Consumer<Object>> subscribers = new ArrayList<Consumer<Object>>();

    Subscription addSubscrition(final Consumer<Object> consumer) {
        for (final Message message : queue)
            consumer.receive(message.body, new LinkedQueueMessageContext(message));
        subscribers.add(consumer);
        return new Subscription() {

            @Override
            public void close() throws IOException {
                subscribers.remove(consumer);
                LinkedQueueConnection.this.close();
            }
        };
    }

    @Override
    public void close() {
        if (subscribers.isEmpty())
            queue.clear();
    }

    @Override
    public <T> T getOne(final Consumer<T> consumer) {
        final Message message = queue.poll();
        if (message != null)
            return consumer.receive(message.body, new LinkedQueueMessageContext(message));
        return null;
    }

    @Override
    public <T> T getOne() {
        return getOne(new SimpleConsumer<T>());
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
            for (final Consumer<Object> consumer : subscribers)
                consumer.receive(body, new LinkedQueueMessageContext(headers, properties));
    }

}
