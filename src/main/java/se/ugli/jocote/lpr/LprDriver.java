package se.ugli.jocote.lpr;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Driver;
import se.ugli.jocote.Message;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.JocoteUrl;

import java.util.function.Consumer;

public class LprDriver implements Driver {

    private static final String URL_SCHEME = "lpr";

    @Override
    public String urlScheme() {
        return URL_SCHEME;
    }

    @Override
    public Connection connect(final JocoteUrl url) {
        return new LprConnection(url);
    }

    @Override
    public Subscription subscribe(final JocoteUrl url, final Consumer<Message> consumer) {
        throw new UnsupportedOperationException("You can't subscribe to logs.");
    }
}
