package se.ugli.jocote.jms;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;

public class JmsMessageTest {

    @Test
    public void jmsMessageID() throws JMSException {
        final TextMessage message = new ActiveMQTextMessage();
        message.setText("asfsDFASDFASDFASD");
        message.setJMSMessageID("hej");
        final JmsMessage msg = new JmsMessage(message);
        assertThat((String) msg.header("MessageID"), equalTo("hej"));
    }

}
