package se.ugli.jocote.jndi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;

public class JndiDriverTest {

    private final InitialContext context = mock(InitialContext.class);
    private final ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
    private final Connection connection = mock(Connection.class);
    private final Session session = mock(Session.class);
    private final Queue destination = mock(Queue.class);

    @Test
    public void test() throws JMSException, NamingException {
        when(context.lookup("FACK")).thenReturn(connectionFactory);
        when(context.lookup("QUEUE")).thenReturn(destination);
        when(connectionFactory.createConnection()).thenReturn(connection);
        when(connection.createSession(false, 1)).thenReturn(session);

        new JndiDriver(context).getQueueConnection("jms:jndi@FACK:QUEUE");

        verify(context).lookup("FACK");
        verify(context).lookup("QUEUE");
    }

}
