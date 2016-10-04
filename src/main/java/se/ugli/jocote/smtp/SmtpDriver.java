package se.ugli.jocote.smtp;

import java.util.function.Consumer;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Driver;
import se.ugli.jocote.Message;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.JocoteUrl;

public class SmtpDriver implements Driver {

    private static final String URL_SCHEME = "smtp";

    @Override
    public String urlScheme() {
        return URL_SCHEME;
    }

    @Override
    public Connection connect(final JocoteUrl url) {
        return new SmtpConnection(url);
    }

    @Override
    public Subscription subscribe(final JocoteUrl url, final Consumer<Message> consumer) {
        throw new UnsupportedOperationException("You can't subscribe to smtp messages.");
    }

}
