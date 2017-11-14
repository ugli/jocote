package se.ugli.jocote.pool.stormpot;

import se.ugli.jocote.Connection;
import se.ugli.jocote.support.ConnectionWrapper;

class PoolableConnectionWrapper extends ConnectionWrapper {

    final PoolableConnection poolableConnection;

    PoolableConnectionWrapper(PoolableConnection poolableConnection) {
        this.poolableConnection = poolableConnection;
    }

    @Override
    public void close() {
        poolableConnection.release();
    }

    @Override
    protected Connection connection() {
        return poolableConnection.connection;
    }

}