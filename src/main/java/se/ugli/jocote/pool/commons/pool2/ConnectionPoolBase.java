package se.ugli.jocote.pool.commons.pool2;

import org.apache.commons.pool2.ObjectPool;

import se.ugli.jocote.Connection;
import se.ugli.jocote.ConnectionPool;

class ConnectionPoolBase implements ConnectionPool {

    final ObjectPool<Connection> objectPool;

    ConnectionPoolBase(ObjectPool<Connection> objectPool) {
        this.objectPool = objectPool;
    }

    @Override
    public Connection connection() {
        return new ObjectPoolConnectionWrapper(objectPool);
    }

    @Override
    public void close() {
        objectPool.close();
    }

}
