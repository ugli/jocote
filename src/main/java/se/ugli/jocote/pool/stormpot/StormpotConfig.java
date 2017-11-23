package se.ugli.jocote.pool.stormpot;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.function.Supplier;

import se.ugli.jocote.Connection;
import se.ugli.jocote.support.ReconnectableConnection;
import stormpot.Config;
import stormpot.Timeout;

public class StormpotConfig extends Config<PoolableConnection> {

    private Timeout claimTimeout = new Timeout(1, MINUTES);
    private Timeout shutdownTimeout = new Timeout(10, SECONDS);

    public StormpotConfig(Supplier<Connection> connectionFactory) {
        setAllocator(new ConnectionAllocator(connectionFactory));
    }

    public StormpotConfig(String url) {
        this(() -> new ReconnectableConnection(url));
    }

    public Timeout getClaimTimeout() {
        return claimTimeout;
    }

    public void setClaimTimeout(Timeout claimTimeout) {
        this.claimTimeout = claimTimeout;
    }

    public Timeout getShutdownTimeout() {
        return shutdownTimeout;
    }

    public void setShutdownTimeout(Timeout shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }

}
