package se.ugli.jocote.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqTest {

    private final static String QUEUE_NAME = "hej";

    public static void main(final String[] args) throws Exception {
        final ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqp://localhost");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        final String message = "Heja världen!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        System.out.println(new String(channel.basicGet(QUEUE_NAME, true).getBody()));
        Thread.sleep(2000);
        channel.close();
        connection.close();
    }

}
