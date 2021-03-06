package se.ugli.jocote.rabbitmq;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Driver;
import se.ugli.jocote.Message;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.JocoteUrl;

import java.util.function.Consumer;

public class RabbitMqDriver implements Driver {

    private static final String URL_SCHEME = "rabbitmq";

    @Override
    public String urlScheme() {
        return URL_SCHEME;
    }

    @Override
    public Connection connect(final JocoteUrl url) {
        return new RabbitMqConnection(url);
    }

    @Override
    public Subscription subscribe(final JocoteUrl url, final Consumer<Message> consumer) {
        return new RabbitMqSubscription(url, consumer);
    }

}
