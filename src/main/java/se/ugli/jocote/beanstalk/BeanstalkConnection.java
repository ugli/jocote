package se.ugli.jocote.beanstalk;

import java.util.Optional;

import com.surftools.BeanstalkClientImpl.ClientImpl;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.support.JocoteUrl;

class BeanstalkConnection implements Connection {

    static final String DEFAULT_SERVER_HOST = "127.0.0.1";
    static final int DEFAULT_SERVER_PORT = 11300;
    static final String PARAM_RESERVE_TIMEOUT_SECONDS = "reserveTimeoutSeconds";

    static final String PUT_HEADER_PRIORITY = "priority";
    static final long DEFAULT_PUT_PRIORITY = 65536;

    static final String PUT_HEADER_DELAY_SECONDS = "delaySeconds";
    static final int DEFAULT_PUT_DELAY_SECONDS = 0;

    static final String PUT_HEADER_TIME_TO_RUN = "timeToRun";
    static final int DEFAULT_PUT_TIME_TO_RUN = 120;

    private final ClientImpl client;
    private final Optional<Integer> reserveTimeoutSeconds;

    BeanstalkConnection(JocoteUrl url) {
        client = new ClientImpl(url.host(DEFAULT_SERVER_HOST), url.port(DEFAULT_SERVER_PORT));
        client.useTube(url.queue);
        reserveTimeoutSeconds = url.paramAsInteger(PARAM_RESERVE_TIMEOUT_SECONDS);
    }

    @Override
    public void close() {
        client.close();
    }

    @Override
    public long clear() {
        return messageStream().count();
    }

    @Override
    public MessageIterator messageIterator() {
        return new BeanstalkMessageIterator(client, reserveTimeoutSeconds);
    }

    @Override
    public void put(Message message) {
        final long priority = message.headerAsLong(PUT_HEADER_PRIORITY, DEFAULT_PUT_PRIORITY);
        final int delaySeconds = message.headerAsInteger(PUT_HEADER_DELAY_SECONDS, DEFAULT_PUT_DELAY_SECONDS);
        final int timeToRun = message.headerAsInteger(PUT_HEADER_TIME_TO_RUN, DEFAULT_PUT_TIME_TO_RUN);
        final byte[] data = message.body();
        client.put(priority, delaySeconds, timeToRun, data);
    }

    @Override
    public long messageCount() {
        throw new UnsupportedOperationException("Beanstalk has no messageCount feature");
    }

    @Override
    public SessionIterator sessionIterator() {
        throw new UnsupportedOperationException("Beanstalk has no session feature");
    }

}
