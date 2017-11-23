package se.ugli.jocote.pool.stormpot;

import stormpot.QueuePool;

public class QueueConnectionPool extends ConnectionPoolBase {

    public QueueConnectionPool(StormpotConfig config) {
        super(config, new QueuePool<>(config));
    }

    public QueueConnectionPool(String url) {
        this(new StormpotConfig(url));
    }
}
