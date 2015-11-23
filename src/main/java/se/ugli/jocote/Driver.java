package se.ugli.jocote;

import se.ugli.jocote.support.JocoteUrl;

public interface Driver {

    boolean acceptsURL(JocoteUrl url);

    Connection getConnection(JocoteUrl url);

    <T> Subscription<T> subscribe(JocoteUrl url, Consumer<T> consumer);

}
