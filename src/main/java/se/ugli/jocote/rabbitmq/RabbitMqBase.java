package se.ugli.jocote.rabbitmq;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

class RabbitMqBase {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private class ChannelCloser implements Runnable {

        final Channel channel;

        ChannelCloser(final Channel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            if (channel != null)
                try {
                    channel.close();
                }
                catch (final RuntimeException | TimeoutException | IOException e) {
                    logger.warn("Couldn't close channel: " + e.getMessage(), e);
                }
        }

    }

    protected void close(final Channel channel) {
        close(channel, null);
    }

    protected void close(final Channel channel, final Integer sessionIteratorChannelCloseDelayMs) {
        if (sessionIteratorChannelCloseDelayMs == null)
            new ChannelCloser(channel).run();
        else
            scheduler.schedule(new ChannelCloser(channel), sessionIteratorChannelCloseDelayMs, TimeUnit.MILLISECONDS);
    }

    protected void close(final Connection connection) {
        try {
            connection.close();
        }
        catch (final RuntimeException | IOException e) {
            logger.warn("Couldn't close connection: " + e.getMessage(), e);
        }
    }

}
