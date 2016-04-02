package se.ugli.jocote.ibm.mq;

import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQQueue;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Driver;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.jms.JmsConnection;
import se.ugli.jocote.jms.JmsSubscription;
import se.ugli.jocote.support.JocoteUrl;

public class IbmMqDriver implements Driver {

    public static final String URL_SCHEME = "ibmmq";
    private static final String TRANSPORT_TYPE_PARAM_NAME = "TransportType";
    private static final int TRANSPORT_TYPE_DEFAULT_VALUE = 1;

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
        try {
            final MQConnectionFactory connectionFactory = new MQConnectionFactory();
            connectionFactory.setTransportType(TRANSPORT_TYPE_DEFAULT_VALUE);
            if (url.host != null)
                connectionFactory.setHostName(url.host);
            if (url.port != null)
                connectionFactory.setPort(url.port);
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

    private Queue queue(final JocoteUrl url) {
        try {
            return new MQQueue(url.queue);
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }
}
