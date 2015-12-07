package se.ugli.jocote;

import se.ugli.jocote.support.JocoteUrl;

public interface Driver {

    String getUrlScheme();

    Connection getConnection(JocoteUrl url);

    <T> Subscription<T> subscribe(JocoteUrl url, Consumer<T> consumer);

}
