package se.ugli.jocote;

public interface Driver {

    boolean acceptsURL(String url);

    Connection getConnection(String url);

    <T> Subscription<T> subscribe(String url, Consumer<T> consumer);

}
