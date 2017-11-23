package se.ugli.jocote.pool.stormpot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.ugli.jocote.Connection;
import se.ugli.jocote.ConnectionPool;
import se.ugli.jocote.JocoteException;
import stormpot.LifecycledPool;
import stormpot.PoolException;

class ConnectionPoolBase implements ConnectionPool {

    static final Logger LOG = LoggerFactory.getLogger(ConnectionPoolBase.class);

    final StormpotConfig config;
    final LifecycledPool<PoolableConnection> pool;

    ConnectionPoolBase(StormpotConfig config, LifecycledPool<PoolableConnection> pool) {
        this.config = config;
        this.pool = pool;
    }

    @Override
    public Connection connection() {
        try {
            return new PoolableConnectionWrapper(pool.claim(config.getClaimTimeout()));
        } catch (PoolException | InterruptedException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public void close() {
        try {
            pool.shutdown().await(config.getShutdownTimeout());
        } catch (final InterruptedException | RuntimeException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

}
