package se.ugli.jocote.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import se.ugli.jocote.Connection;
import se.ugli.jocote.Driver;
import se.ugli.jocote.Message;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.jms.JmsConnection;
import se.ugli.jocote.jms.JmsSubscription;
import se.ugli.jocote.support.JocoteUrl;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import java.util.function.Consumer;

public class ActiveMqDriver implements Driver {

    private static final String URL_SCHEME = "activemq";

    @Override
    public String urlScheme() {
        return URL_SCHEME;
    }

    @Override
    public Connection connect(final JocoteUrl url) {
        return new JmsConnection(connectionFactory(url), queue(url), url);
    }

    @Override
    public Subscription subscribe(final JocoteUrl url, final Consumer<Message> consumer) {
        return new JmsSubscription(connectionFactory(url), consumer, queue(url));
    }

    private ConnectionFactory connectionFactory(final JocoteUrl url) {
        final String host = url.host != null ? url.host : "localhost";
        final int port = url.port != null ? url.port : 61616;
        return new ActiveMQConnectionFactory("tcp://" + host + ":" + port);
    }

    private Queue queue(final JocoteUrl url) {
        return new ActiveMQQueue(url.queue);
    }
}
