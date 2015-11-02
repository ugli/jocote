package se.ugli.jocote.activemq;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.Driver;
import se.ugli.jocote.QueueConnection;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.jms.JmsQueueConnection;
import se.ugli.jocote.jms.JmsSubscription;

public class ActiveMqDriver implements Driver {

    @Override
    public boolean acceptsURL(final String url) {
        return ActiveMqUrl.acceptsURL(url);
    }

    @Override
    public QueueConnection getQueueConnection(final String urlStr) {
        final ActiveMqUrl url = new ActiveMqUrl(urlStr);
        return new JmsQueueConnection(connectionFactory(url), queue(url));
    }

    @Override
    public <T> Subscription<T> subscribe(final String urlStr, final Consumer<T> consumer) {
        final ActiveMqUrl url = new ActiveMqUrl(urlStr);
        return new JmsSubscription<T>(connectionFactory(url), consumer, queue(url));
    }

    private ConnectionFactory connectionFactory(final ActiveMqUrl url) {
        return new ActiveMQConnectionFactory("tcp://" + url.host + ":" + url.port);
    }

    private Queue queue(final ActiveMqUrl url) {
        return new ActiveMQQueue(url.queue);
    }
}
