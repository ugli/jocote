package se.ugli.jocote.pool.commons.pool2;

import java.util.function.Supplier;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import se.ugli.jocote.Connection;
import se.ugli.jocote.support.ReconnectableConnection;

public class SizedConnectionPool extends ConnectionPoolBase {

    public SizedConnectionPool(Supplier<Connection> connectionSupplier, GenericObjectPoolConfig config) {
        super(createPool(connectionSupplier, config));
    }

    public SizedConnectionPool(String url, GenericObjectPoolConfig config) {
        this(() -> new ReconnectableConnection(url), config);
    }

    public SizedConnectionPool(String url) {
        this(url, new GenericObjectPoolConfig());
    }

    private static ObjectPool<Connection> createPool(Supplier<Connection> connectionSupplier,
            GenericObjectPoolConfig config) {
        final PooledConnectionFactory factory = new PooledConnectionFactory(connectionSupplier);
        return new GenericObjectPool<>(factory, config);
    }

}
