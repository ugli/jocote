package se.ugli.jocote.pool.commons.pool2;

import java.util.function.Supplier;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import se.ugli.jocote.Connection;
import se.ugli.jocote.support.ReconnectableConnection;

public class CommonsPoolConfig extends GenericObjectPoolConfig {

    final Supplier<Connection> connectionFactory;

    public CommonsPoolConfig(Supplier<Connection> connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public CommonsPoolConfig(String url) {
        this(() -> new ReconnectableConnection(url));
    }

}
