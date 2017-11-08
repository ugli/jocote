package se.ugli.jocote.pool.commons.pool2;

import org.apache.commons.pool2.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.ugli.jocote.Connection;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.support.ConnectionWrapper;

class ObjectPoolConnectionWrapper extends ConnectionWrapper {

    static final Logger LOG = LoggerFactory.getLogger(ObjectPoolConnectionWrapper.class);
    final ObjectPool<Connection> pool;

    ObjectPoolConnectionWrapper(ObjectPool<Connection> pool) {
        super(uncheckedBorrow(pool));
        this.pool = pool;
    }

    static Connection uncheckedBorrow(ObjectPool<Connection> pool) {
        try {
            return pool.borrowObject();
        } catch (final Exception e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public void close() {
        try {
            pool.returnObject(connection);
        } catch (final Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }

}