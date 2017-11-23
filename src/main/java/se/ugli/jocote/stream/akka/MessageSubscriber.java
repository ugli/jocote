package se.ugli.jocote.stream.akka;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Jocote;
import se.ugli.jocote.Message;

class MessageSubscriber implements Subscriber<Message> {

    private final String url;
    private Connection connection;

    MessageSubscriber(final String url) {
        this.url = url;
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
        connection = Jocote.connect(url);
        subscription.request(Long.MAX_VALUE); // TODO
    }

    @Override
    public void onNext(final Message message) {
        connection.put(message);
    }

    @Override
    public void onError(final Throwable error) {
        error.printStackTrace();
    }

    @Override
    public void onComplete() {
        connection.close();
    }

}
