package se.ugli.jocote.pool.stormpot;

import se.ugli.jocote.Connection;
import stormpot.Poolable;
import stormpot.Slot;

class PoolableConnection implements Poolable {

    final Connection connection;
    final Slot slot;

    PoolableConnection(Slot slot, Connection connection) {
        this.slot = slot;
        this.connection = connection;
    }

    @Override
    public void release() {
        slot.release(this);
    }

}
