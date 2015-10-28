package se.ugli.jocote;

public interface Driver {

    boolean acceptsURL(String url);

    Connection getConnection(String url);

    Subscription subscribe(String url, Consumer<Object> consumer);

}
