package se.ugli.jocote;

import java.util.function.Consumer;

public interface Driver {

    String getUrlScheme();

    Connection getConnection(JocoteUrl url);

    Subscription subscribe(JocoteUrl url, Consumer<Message> consumer);

}
