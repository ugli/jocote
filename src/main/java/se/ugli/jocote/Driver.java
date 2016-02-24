package se.ugli.jocote;

import java.util.function.Consumer;

import se.ugli.jocote.support.JocoteUrl;

public interface Driver {

    String getUrlScheme();

    Connection getConnection(JocoteUrl url);

    Subscription subscribe(JocoteUrl url, Consumer<Message> consumer);

}
