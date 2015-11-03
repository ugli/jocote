package se.ugli.jocote.ibmmq;

import java.util.Map.Entry;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;

import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnectionFactory;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.Driver;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.QueueConnection;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.jms.JmsQueueConnection;
import se.ugli.jocote.jms.JmsSubscription;

public class IbmMqDriver implements Driver {

    @Override
    public boolean acceptsURL(final String url) {
        return IbmMqUrl.acceptsURL(url);
    }

    @Override
    public QueueConnection getQueueConnection(final String urlStr) {
        final IbmMqUrl url = new IbmMqUrl(urlStr);
        return new JmsQueueConnection(connectionFactory(url), queue(url));
    }

    @Override
    public <T> Subscription<T> subscribe(final String urlStr, final Consumer<T> consumer) {
        final IbmMqUrl url = new IbmMqUrl(urlStr);
        return new JmsSubscription<T>(connectionFactory(url), consumer, queue(url));
    }

    private ConnectionFactory connectionFactory(final IbmMqUrl url) {
        try {
            final MQQueueConnectionFactory connectionFactory = new MQQueueConnectionFactory();
            for (final Entry<String, String> param : url.params.entrySet())
                connectionFactory.setStringProperty(param.getKey(), param.getValue());
            return connectionFactory;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    private Queue queue(final IbmMqUrl url) {
        try {
            return new MQQueue(url.queue);
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }
}
