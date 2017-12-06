package se.ugli.jocote.reactivestreams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Jocote;
import se.ugli.jocote.Message;

class MessagePublisher implements Publisher<Message> {

    static final Logger LOG = LoggerFactory.getLogger(MessagePublisher.class);
    final String url;

    MessagePublisher(final String url) {
        this.url = url;
    }

    @Override
    public void subscribe(final Subscriber<? super Message> subscriber) {
        try {
            subscriber.onSubscribe(new MessageSubscription(subscriber, url));
        } catch (RuntimeException e) {
            LOG.error(e.getMessage(), e);
        }
    }
    
    static class MessageSubscription implements Subscription {

        final Subscriber<? super Message> downstream;
        final Connection connection;

        MessageSubscription(Subscriber<? super Message> downstream, String url) {
            this.downstream = downstream;
            connection = Jocote.connect(url);
        }

        @Override
        public void request(long n) {
            try {
                connection.messageStream().limit(n).forEach(downstream::onNext);
            } catch (RuntimeException e) {
                LOG.error(e.getMessage(), e);
                try {
                    connection.close();
                } catch (RuntimeException e1) {
                    LOG.error(e1.getMessage(), e1);
                }
                try {
                    downstream.onError(e);
                } catch (RuntimeException e1) {
                    LOG.error(e1.getMessage(), e1);
                }
            }
        }

        @Override
        public void cancel() {
            try {
                connection.close();
            } catch (RuntimeException e1) {
                LOG.error(e1.getMessage(), e1);
            }
        }

    }

}