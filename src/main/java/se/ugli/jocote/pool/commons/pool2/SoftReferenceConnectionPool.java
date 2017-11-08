package se.ugli.jocote.pool.commons.pool2;

import java.util.function.Supplier;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.SoftReferenceObjectPool;

import se.ugli.jocote.Connection;
import se.ugli.jocote.support.ReconnectableConnection;

public class SoftReferenceConnectionPool extends ConnectionPoolBase {

    public SoftReferenceConnectionPool(String url) {
        this(() -> new ReconnectableConnection(url));
    }

    public SoftReferenceConnectionPool(Supplier<Connection> connectionSupplier) {
        super(createPool(connectionSupplier));
    }

    private static ObjectPool<Connection> createPool(Supplier<Connection> connectionSupplier) {
        return new SoftReferenceObjectPool<>(new PooledConnectionFactory(connectionSupplier));
    }

}
