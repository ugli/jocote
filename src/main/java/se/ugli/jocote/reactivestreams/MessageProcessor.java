package se.ugli.jocote.reactivestreams;

import static se.ugli.jocote.Jocote.defaultExecutor;

import java.util.concurrent.Executor;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import se.ugli.jocote.Message;

public class MessageProcessor implements Processor<Message, Message> {

    private MessagePublisher messagePublisher;
    private MessageSubscriber messageSubscriber;

    private MessageProcessor(Executor executor, String url) {
        messagePublisher = new MessagePublisher(url);
        messageSubscriber = new MessageSubscriber(executor, url);
    }

    public static MessageProcessor apply(Executor executor, String url) {
        return new MessageProcessor(executor, url);
    }

    public static MessageProcessor apply(String url) {
        return apply(defaultExecutor, url);
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        messageSubscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(Message message) {
        messageSubscriber.onNext(message);
    }

    @Override
    public void onError(Throwable error) {
        messageSubscriber.onError(error);
    }

    @Override
    public void onComplete() {
        messageSubscriber.onComplete();
    }

    @Override
    public void subscribe(Subscriber<? super Message> subscriber) {
        messagePublisher.subscribe(subscriber);
    }

}
