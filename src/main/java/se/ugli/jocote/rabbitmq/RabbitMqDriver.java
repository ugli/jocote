package se.ugli.jocote.rabbitmq;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Consumer;
import se.ugli.jocote.Driver;
import se.ugli.jocote.JocoteUrl;
import se.ugli.jocote.Subscription;

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
    public <T> Subscription<T> subscribe(final JocoteUrl url, final Consumer<T> consumer) {
        return new RabbitSubscription<T>(url, consumer);
    }

}
