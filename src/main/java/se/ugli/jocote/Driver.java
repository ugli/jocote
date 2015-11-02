package se.ugli.jocote;

public interface Driver {

    boolean acceptsURL(String url);

    QueueConnection getQueueConnection(String url);

    <T> Subscription<T> subscribe(String url, Consumer<T> consumer);

}
