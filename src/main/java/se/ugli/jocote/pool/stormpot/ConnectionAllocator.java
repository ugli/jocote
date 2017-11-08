package se.ugli.jocote.pool.stormpot;

import java.util.function.Supplier;

import se.ugli.jocote.Connection;
import stormpot.Allocator;
import stormpot.Slot;

class ConnectionAllocator implements Allocator<PoolableConnection> {

    final Supplier<Connection> connectionFactory;

    ConnectionAllocator(Supplier<Connection> connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public PoolableConnection allocate(Slot slot) throws Exception {
        return new PoolableConnection(slot, connectionFactory.get());
    }

    @Override
    public void deallocate(PoolableConnection poolable) throws Exception {
        poolable.connection.close();
    }

}
