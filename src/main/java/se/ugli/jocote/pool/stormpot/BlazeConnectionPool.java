package se.ugli.jocote.pool.stormpot;

import stormpot.BlazePool;

public class BlazeConnectionPool extends ConnectionPoolBase {

    public BlazeConnectionPool(StormpotConfig config) {
        super(config, new BlazePool<>(config));
    }

    public BlazeConnectionPool(String url) {
        this(new StormpotConfig(url));
    }

}
