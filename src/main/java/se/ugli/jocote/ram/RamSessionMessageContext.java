package se.ugli.jocote.ram;

import java.util.Queue;
import java.util.Set;

import se.ugli.jocote.SessionMessageContext;

class RamSessionMessageContext implements SessionMessageContext {
    private final Queue<Message> connectionQueue;
    private final RamMessageContext cxt;
    private final Message message;

    RamSessionMessageContext(final Message message, final Queue<Message> connectionQueue) {
        this.message = message;
        this.connectionQueue = connectionQueue;
        cxt = new RamMessageContext(message);
    }

    @Override
    public void acknowledgeMessage() {

    }

    @Override
    public <H> H getHeader(final String headerName) {
        return cxt.getHeader(headerName);
    }

    @Override
    public Set<String> getHeaderNames() {
        return cxt.getHeaderNames();
    }

    @Override
    public <P> P getProperty(final String propertyName) {
        return cxt.getProperty(propertyName);
    }

    @Override
    public Set<String> getPropertyNames() {
        return cxt.getPropertyNames();
    }

    @Override
    public void leaveMessage() {
        connectionQueue.offer(message);
    }

}
