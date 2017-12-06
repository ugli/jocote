package se.ugli.jocote.reactivestreams;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.stream.IntStream;

import org.junit.Test;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Jocote;
import se.ugli.jocote.Message;

public class MessageProcessorTest {

    private static final String URL = "ram:/APA";
    private MessageProcessor messageProcessor = MessageProcessor.apply(URL);
    private Connection connection = Jocote.connect(URL);

    @Test
    public void shouldSubscribe() throws InterruptedException {
        MessageSubscriptionBuilder builder = MessageSubscriptionBuilder.apply(messageProcessor);
        builder.add(Message.builder().body("hej".getBytes()).build());
        messageProcessor.onSubscribe(builder.build());
        Thread.sleep(100);
        assertThat(new String(connection.get().get().body()), equalTo("hej"));
    }

    @Test
    public void shouldSubscribe10() throws InterruptedException {
        MessageSubscriptionBuilder builder = MessageSubscriptionBuilder.apply(messageProcessor);
        IntStream.range(0, 10).forEach(i -> builder.add(Message.builder().body("hej".getBytes()).build()));
        messageProcessor.onSubscribe(builder.build());
        Thread.sleep(100);
        assertThat(connection.messageCount(), equalTo(10L));
    }

}
