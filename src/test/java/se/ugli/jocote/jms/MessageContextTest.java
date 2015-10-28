package se.ugli.jocote.jms;

import static org.junit.Assert.assertThat;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class MessageContextTest {

    @Test
    public void jmsMessageID() throws JMSException {
        final TextMessage message = new ActiveMQTextMessage();
        message.setJMSMessageID("hej");
        final JmsMessageContext context = new JmsMessageContext(message);
        final Map<String, Object> headers = context.getHeaders();
        assertThat((String) headers.get("MessageID"), CoreMatchers.equalTo("hej"));
    }

}
