package se.ugli.jocote.ibm.mq;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQQueue;
import se.ugli.jocote.*;
import se.ugli.jocote.jms.JmsConnection;
import se.ugli.jocote.jms.JmsSubscription;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import java.util.Map.Entry;

public class IbmMqDriver implements Driver {

    private static final String TRANSPORT_TYPE_PARAM_NAME = "TransportType";
    private static final int TRANSPORT_TYPE_DEFAULT_VALUE = 1;

    @Override
    public boolean acceptsURL(final String url) {
        return IbmMqUrl.acceptsURL(url);
    }

    @Override
    public Connection getConnection(final String urlStr) {
        final IbmMqUrl url = new IbmMqUrl(urlStr);
        return new JmsConnection(connectionFactory(url), queue(url));
    }

    @Override
    public <T> Subscription<T> subscribe(final String urlStr, final Consumer<T> consumer) {
        final IbmMqUrl url = new IbmMqUrl(urlStr);
        return new JmsSubscription<T>(connectionFactory(url), consumer, queue(url));
    }

    private ConnectionFactory connectionFactory(final IbmMqUrl url) {
        try {
            final MQConnectionFactory connectionFactory = new MQConnectionFactory();
            connectionFactory.setTransportType(TRANSPORT_TYPE_DEFAULT_VALUE);
            for (final Entry<String, String> param : url.params.entrySet())
                if (param.getKey().equalsIgnoreCase(TRANSPORT_TYPE_PARAM_NAME))
                    connectionFactory.setTransportType(Integer.parseInt(param.getValue()));
                else
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
