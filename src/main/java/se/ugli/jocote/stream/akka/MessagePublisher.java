package se.ugli.jocote.stream.akka;

import java.util.Optional;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import scala.concurrent.ExecutionContext;
import se.ugli.jocote.Connection;
import se.ugli.jocote.Jocote;
import se.ugli.jocote.Message;

class MessagePublisher implements Publisher<Message> {

    private final String url;
    private final ExecutionContext executionContext;

    MessagePublisher(final String url, final ExecutionContext executionContext) {
        this.url = url;
        this.executionContext = executionContext;
    }

    @Override
    public void subscribe(final Subscriber<? super Message> subscriber) {
        subscriber.onSubscribe(new MessageSubscription(url, subscriber, executionContext));
    }

    static class MessageSubscription implements Subscription {

        private final Connection connection;
        private final Subscriber<? super Message> subscriber;
        private final ExecutionContext executionContext;

        MessageSubscription(final String url, final Subscriber<? super Message> subscriber,
                final ExecutionContext executionContext) {
            connection = Jocote.connect(url);
            this.subscriber = subscriber;
            this.executionContext = executionContext;
        }

        @Override
        public void cancel() {
            connection.close();
        }

        @Override
        public void request(final long numOfItems) {
            executionContext.execute(() -> consume(numOfItems));
        }

        void consume(final long numOfItems) {
            try {
                for (long i = 0; i < numOfItems; i++) {
                    final Optional<Message> msgOpt = connection.get();
                    if (msgOpt.isPresent())
                        subscriber.onNext(msgOpt.get());
                    else
                        break;
                }
                subscriber.onComplete();
            }
            catch (final RuntimeException e) {
                subscriber.onError(e);
            }
        }

    }

}
