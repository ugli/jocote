package se.ugli.jocote.reactivestreams;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.runAsync;
import static se.ugli.jocote.Jocote.connect;

import java.util.Optional;
import java.util.concurrent.Executor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Message;

class MessageSubscriber implements Subscriber<Message> {

    private static final int REQ_SIZE = 1;
    static final Logger LOG = LoggerFactory.getLogger(MessageSubscriber.class);
    final String url;
    final Executor executor;

    private Subscription subscription;
    private Connection connection;

    MessageSubscriber(Executor executor, String url) {
        this.executor = executor;
        this.url = url;
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        runAsync(() -> {
            try {
                Optional<Subscription> optSub = ofNullable(subscription);
                if (optSub.isPresent()) {
                    connection = connect(url);
                    this.subscription = subscription;
                    subscription.request(REQ_SIZE);
                } else
                    subscription.cancel();
            } catch (RuntimeException e) {
                LOG.error(e.getMessage(), e);
            }
        }, executor);
    }

    @Override
    public void onNext(Message t) {
        try {
            connection.put(t).thenAcceptAsync(v -> {
                ofNullable(subscription).ifPresent(s -> {
                    try {
                        s.request(REQ_SIZE);
                    } catch (RuntimeException e) {
                        LOG.error(e.getMessage(), e);
                    }
                });

            }, executor);
        } catch (RuntimeException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void onError(Throwable e) {
        runAsync(() -> {
            LOG.error(e.getMessage(), e);
        }, executor);
    }

    @Override
    public void onComplete() {
        runAsync(() -> {
            try {
                ofNullable(connection).ifPresent(Connection::close);
            } catch (RuntimeException e) {
                LOG.error(e.getMessage(), e);
            } finally {
                subscription = null;
                connection = null;
            }
        }, executor);
    }

}