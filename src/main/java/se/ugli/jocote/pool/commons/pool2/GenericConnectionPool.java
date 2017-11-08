package se.ugli.jocote.pool.commons.pool2;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import se.ugli.jocote.Connection;

public class GenericConnectionPool extends ConnectionPoolBase {

    public GenericConnectionPool(CommonsPoolConfig config) {
        super(objectPool(config));
    }

    public GenericConnectionPool(String url) {
        this(new CommonsPoolConfig(url));
    }

    private static ObjectPool<Connection> objectPool(CommonsPoolConfig config) {
        return new GenericObjectPool<>(new PooledConnectionFactory(config.connectionFactory), config);
    }

}
