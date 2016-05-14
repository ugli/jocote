package se.ugli.jocote.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.support.JocoteUrl;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

class ClientConnectionFactory {


    private final static AtomicInteger threadNumber = new AtomicInteger(1);

    public static Connection create(JocoteUrl url) {
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
            factory.setAutomaticRecoveryEnabled(true);
            factory.setSharedExecutor(newSingleThreadExecutor());
            factory.setShutdownExecutor(newSingleThreadExecutor());
            factory.setThreadFactory(ClientConnectionFactory::newThread);
            return factory.newConnection();
        } catch (IOException | TimeoutException e) {
            throw new JocoteException(e);
        }
    }

    private static Thread newThread(Runnable r) {
        return new Thread(r, "jocote-rabbitmq-connection-" + threadNumber.getAndIncrement());
    }

}
