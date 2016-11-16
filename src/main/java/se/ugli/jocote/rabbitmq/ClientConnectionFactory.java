package se.ugli.jocote.rabbitmq;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.StrictExceptionHandler;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.support.JocoteUrl;

class ClientConnectionFactory {

    private final static AtomicInteger threadNumber = new AtomicInteger(1);

    static Connection create(final JocoteUrl url, final boolean automaticRecoveryEnabled) {
        try {
            final ConnectionFactory factory = new ConnectionFactory();
            if (url.host != null)
                factory.setHost(url.host);
            if (url.port != null)
                factory.setPort(url.port);
            if (url.username != null)
                factory.setUsername(url.username);
            if (url.password != null)
                factory.setPassword(url.password);
            factory.setAutomaticRecoveryEnabled(automaticRecoveryEnabled);
            factory.setSharedExecutor(newSingleThreadExecutor());
            factory.setShutdownExecutor(newSingleThreadExecutor());
            factory.setThreadFactory(ClientConnectionFactory::newThread);
            factory.setExceptionHandler(new ExceptionHandler());
            return factory.newConnection();
        }
        catch (IOException | TimeoutException e) {
            throw new JocoteException(e);
        }
    }

    private static class ExceptionHandler extends StrictExceptionHandler {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        protected void handleChannelKiller(final Channel channel, final Throwable exception, final String what) {
            super.handleChannelKiller(channel, exception, what);
            logger.error(what + " threw an exception for channel " + channel, exception);
        }

    }

    private static Thread newThread(final Runnable r) {
        return new Thread(r, "jocote-rabbitmq-connection-" + threadNumber.getAndIncrement());
    }

}
