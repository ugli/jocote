package se.ugli.jocote.reactivestreams;

import static java.util.concurrent.CompletableFuture.runAsync;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import se.ugli.jocote.Jocote;
import se.ugli.jocote.Message;

public class MessageSubscriptionBuilder {

    private final List<Message> messages = new LinkedList<>();
    private final Subscriber<Message> subscriber;
    private final Executor executor;

    private MessageSubscriptionBuilder(Subscriber<Message> subscriber, Executor executor) {
        this.subscriber = subscriber;
        this.executor = executor;
    }

    public static MessageSubscriptionBuilder apply(Subscriber<Message> subscriber) {
        return apply(subscriber, Jocote.defaultExecutor);
    }

    public static MessageSubscriptionBuilder apply(Subscriber<Message> subscriber, Executor executor) {
        return new MessageSubscriptionBuilder(subscriber, executor);
    }

    public MessageSubscriptionBuilder add(Message message) {
        messages.add(message);
        return this;
    }

    public Subscription build() {
        return new MessageSubscription(messages.iterator());
    }

    class MessageSubscription implements Subscription {

        private Iterator<Message> messages;

        public MessageSubscription(Iterator<Message> messages) {
            this.messages = messages;
        }

        @Override
        public void request(long n) {
            runAsync(() -> {
                long numOfMsg = n;
                for (long i = 0; i < numOfMsg && messages.hasNext(); numOfMsg++)
                    subscriber.onNext(messages.next());
                if (!messages.hasNext())
                    subscriber.onComplete();
            }, executor);
        }

        @Override
        public void cancel() {
        }

    }

}
