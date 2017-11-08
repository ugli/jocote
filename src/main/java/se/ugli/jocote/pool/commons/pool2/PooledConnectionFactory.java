package se.ugli.jocote.pool.commons.pool2;

import java.util.function.Supplier;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import se.ugli.jocote.Connection;

class PooledConnectionFactory extends BasePooledObjectFactory<Connection> {

    private final Supplier<Connection> connectionSupplier;

    PooledConnectionFactory(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    @Override
    public Connection create() {
        return connectionSupplier.get();
    }

    @Override
    public void destroyObject(final PooledObject<Connection> p) {
        p.getObject().close();
    }

    @Override
    public PooledObject<Connection> wrap(final Connection connection) {
        return new DefaultPooledObject<>(connection);
    }
}