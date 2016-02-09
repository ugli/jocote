package se.ugli.jocote;

public interface Driver {

    String getUrlScheme();

    Connection getConnection(JocoteUrl url);

    <T> Subscription<T> subscribe(JocoteUrl url, Consumer<T> consumer);

}
