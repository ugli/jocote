package se.ugli.jocote.pool.commons.pool2;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.SoftReferenceObjectPool;

import se.ugli.jocote.Connection;

public class SoftReferenceConnectionPool extends ConnectionPoolBase {

    public SoftReferenceConnectionPool(String url) {
        this(new CommonsPoolConfig(url));
    }

    public SoftReferenceConnectionPool(CommonsPoolConfig config) {
        super(objectPool(config));
    }

    private static ObjectPool<Connection> objectPool(CommonsPoolConfig config) {
        return new SoftReferenceObjectPool<>(new PooledConnectionFactory(config.connectionFactory));
    }

}
