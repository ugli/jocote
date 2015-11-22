package se.ugli.jocote.jndi;

import org.junit.Test;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.mockito.Mockito.*;

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

        new JndiDriver(context).getConnection("jms:jndi@FACK:QUEUE");

        verify(context).lookup("FACK");
        verify(context).lookup("QUEUE");
    }

}
