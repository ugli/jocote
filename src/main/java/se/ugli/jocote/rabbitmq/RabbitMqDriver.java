package se.ugli.jocote.rabbitmq;

import java.util.function.Consumer;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Driver;
import se.ugli.jocote.Message;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.JocoteUrl;

public class RabbitMqDriver implements Driver {

    public static final String URL_SCHEME = "rabbitmq";

    @Override
    public String getUrlScheme() {
        return URL_SCHEME;
    }

    @Override
    public Connection getConnection(final JocoteUrl url) {
        return new RabbitMqConnection(url);
    }

    @Override
    public Subscription subscribe(final JocoteUrl url, final Consumer<Message> consumer) {
        return new RabbitSubscription(url, consumer);
    }

}
