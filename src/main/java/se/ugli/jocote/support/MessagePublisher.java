package se.ugli.jocote.support;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Message;

public class MessagePublisher implements Publisher<Message> {

    private final Connection connection;
    private final ExecutorService executorService;

    public MessagePublisher(Connection connection) {
        this(connection, newSingleThreadExecutor());
    }

    public MessagePublisher(Connection connection, ExecutorService executorService) {
        this.connection = connection;
        this.executorService = executorService;
    }

    @Override
    public void subscribe(Subscriber<? super Message> subscriber) {
        subscriber.onSubscribe(new MessageSubscription(connection, subscriber, executorService));
    }

    static class MessageSubscription implements Subscription {

        private final Connection connection;
        private final Subscriber<? super Message> subscriber;
        private final ExecutorService executorService;

        MessageSubscription(Connection connection, Subscriber<? super Message> subscriber,
                ExecutorService executorService) {
            this.connection = connection;
            this.subscriber = subscriber;
            this.executorService = executorService;
        }

        @Override
        public void cancel() {
            connection.close();
            executorService.shutdown();
        }

        @Override
        public void request(long numOfItems) {
            executorService.execute(() -> consume(numOfItems));
        }

        void consume(long numOfItems) {
            try {
                for (long i = 0; i < numOfItems; i++) {
                    final Optional<Message> msgOpt = connection.get();
                    if (msgOpt.isPresent()) {
                        subscriber.onNext(msgOpt.get());
                    } else {
                        break;
                    }
                }
                subscriber.onComplete();
            } catch (final RuntimeException e) {
                subscriber.onError(e);
            }
        }

    }

}
