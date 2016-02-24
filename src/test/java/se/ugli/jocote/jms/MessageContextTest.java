package se.ugli.jocote.jms;

import static org.junit.Assert.assertThat;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class MessageContextTest {

    @Test
    public void jmsMessageID() throws JMSException {
        final TextMessage message = new ActiveMQTextMessage();
        message.setText("asfsDFASDFASDFASD");
        message.setJMSMessageID("hej");
        final JmsMessageContext context = new JmsMessageContext(message);
        assertThat((String) context.getHeader("MessageID"), CoreMatchers.equalTo("hej"));
    }

}
