package se.ugli.jocote.stream.flow;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Message;

public class MessageSubscriber implements Subscriber<Message> {

    private final Connection connection;

    public MessageSubscriber(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public void onComplete() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onError(final Throwable error) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNext(final Message message) {
        connection.put(message);
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        // TODO Auto-generated method stub

    }

}
