package se.ugli.jocote;

public interface Driver {

    String getUrlScheme();

    Connection getConnection(JocoteUrl url);

    Subscription subscribe(JocoteUrl url, Consumer<byte[]> consumer);

}
