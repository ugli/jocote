package se.ugli.jocote;

import static java.lang.Integer.parseInt;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import se.ugli.jocote.rabbitmq.RabbitMqProperties;

@RunWith(Parameterized.class)
public class DriverTest {

    @SuppressWarnings("rawtypes")
    @Parameterized.Parameters(name = "{0}")
    public static Collection testArray() {
        return Arrays.asList(new Object[][] { { "ActiveMQ", "activemq:/APA" }, { "RAM", "ram:/APA" }, { "RabbitMQ", "rabbitmq:/APA" } });
    }

    private Connection connection;
    private final String url;
    private final String testName;

    public DriverTest(final String testName, final String url) {
        this.testName = testName;
        this.url = url;
    }

    @Before
    public void clearQueue() {
        try (Connection c = DriverManager.connect(url)) {
            c.clear();
        }
        connection = DriverManager.connect(url);
    }

    @After
    public void close() {
        connection.close();
        connection = null;
    }

    @Test
    public void shouldHandleGetAndPutBytes() {
        assertThat(connection.get().isPresent(), equalTo(false));
        connection.put("hej".getBytes());
        assertThat(new String(connection.get().get().body()), equalTo("hej"));
    }

    @Test
    public void shouldGetJmsHeaderValue() {
        assumeFalse(testName.equals("RabbitMQ"));
        connection.put(Message.builder().body("hej".getBytes()).header("CorrelationID", "B").build());
        assertThat(connection.get().get().headers().get("CorrelationID"), equalTo("B"));
    }

    @Test
    public void shouldGetRabbitHeaderStringValue() {
        assumeTrue(testName.equals("RabbitMQ"));
        connection.put(Message.builder().body("hej".getBytes()).header("Hepp", "B").build());
        final Optional<Message> msgOpt = connection.get();
        assertThat(msgOpt.get().headers().get("Hepp"), equalTo("B"));
    }

    @Test
    public void shouldGetRabbitHeaderIntValue() {
        assumeTrue(testName.equals("RabbitMQ"));
        connection.put(Message.builder().body("hej".getBytes()).header("Hepp", 7).build());
        assertThat(connection.get().get().headers().get("Hepp"), equalTo(7));
    }

    @Test
    public void shouldGetJmsPropertyValue() {
        assumeFalse(testName.equals("RabbitMQ"));
        connection.put(Message.builder().body("hej".getBytes()).property("environment", "TEST").build());
        assertThat(connection.get().get().properties().get("environment"), equalTo("TEST"));
    }

    @Test
    public void shouldGetRabbitMqCorrelationId() {
        assumeTrue(testName.equals("RabbitMQ"));
        connection.put(Message.builder().body("hej".getBytes()).property(RabbitMqProperties.CorrelationId, "B").build());
        assertThat(connection.get().get().properties().get(RabbitMqProperties.CorrelationId.name()), equalTo("B"));
    }

    @Test
    public void shouldGetRabbitMqDeliveryMode() {
        assumeTrue(testName.equals("RabbitMQ"));
        connection.put(Message.builder().body("hej".getBytes()).property(RabbitMqProperties.DeliveryMode, 2).build());
        assertThat(connection.get().get().properties().get(RabbitMqProperties.DeliveryMode.name()), equalTo(2));
    }

    @Test
    public void shouldAcknowledgeMessage() {
        connection.put("hej".getBytes());
        connection.get(session -> {
            session.ack();
            return Optional.of(session.message());
        });
        assertThat(connection.get().isPresent(), equalTo(false));
    }

    @Test
    public void shouldLeaveMessage() {
        connection.put("hej".getBytes());
        connection.get(session -> {
            session.nack();
            return Optional.of(session.message());
        });
        assertThat(new String(connection.get().get().body()), equalTo("hej"));
    }

    @Test
    public void shouldThrowThenNotLeavingOrAcknowledgeMessage() {
        try {
            connection.put("hej".getBytes());
            connection.get(session -> Optional.of(session.message()));
        }
        catch (final JocoteException e) {
            assertThat(e.getMessage(), equalTo("You have to acknowledge or leave message"));
        }
    }

    @Test
    public void shouldConsumeStream() {
        for (int i = 0; i <= 100; i++)
            connection.put(String.valueOf(i).getBytes());
        assertThat(connection.messageStream(200).mapToInt(m -> parseInt(new String(m.body()))).sum(), equalTo(5050));
        assertThat(connection.get().isPresent(), equalTo(false));
    }

    @Test
    public void shouldAcknoledgeStream() {
        for (int i = 0; i <= 100; i++)
            connection.put(String.valueOf(i).getBytes());
        try (SessionStream stream = connection.sessionStream(200)) {
            assertThat(stream.mapToInt(m -> parseInt(new String(m.body()))).sum(), equalTo(5050));
            stream.ack();
        }
    }

    @Test
    public void shouldLimitStream() {
        for (int i = 0; i <= 100; i++)
            connection.put(String.valueOf(i).getBytes());
        final MessageStream stream = connection.messageStream(10);
        assertThat(stream.mapToInt(m -> parseInt(new String(m.body()))).sum(), equalTo(45));
        assertThat(stream.elementIndex(), is(10));
    }

    @Test
    public void shouldLimitSessionStream() {
        for (int i = 0; i <= 100; i++)
            connection.put(String.valueOf(i).getBytes());
        try (SessionStream stream = connection.sessionStream(10)) {
            assertThat(stream.mapToInt(m -> parseInt(new String(m.body()))).sum(), equalTo(45));
            stream.ack();
            assertThat(stream.elementIndex(), is(10));
        }
    }

    @Test
    public void shouldLeaveStream() {
        for (int i = 0; i < 100; i++)
            connection.put(String.valueOf(i).getBytes());
        try (SessionStream stream = connection.sessionStream()) {
            assertThat(stream.mapToInt(m -> parseInt(new String(m.body()))).sum(), equalTo(4950));
            stream.nack();
        }
        assertThat(new String(connection.get().get().body()), equalTo("0"));
    }

    @Test
    public void shouldThrowThenNotLeavingOrAcknowledgeMessageStream() {
        for (int i = 0; i <= 100; i++)
            connection.put(String.valueOf(i).getBytes());
        try (SessionStream stream = connection.sessionStream(200)) {
            assertThat(stream.mapToInt(m -> parseInt(new String(m.body()))).sum(), equalTo(5050));
        }
        catch (final JocoteException e) {
            assertThat(e.getMessage(), equalTo("You have to acknowledge or leave messages before closing"));
        }
    }

    private class IntWrap {
        int i = 0;

        @Override
        public String toString() {
            return "IntWrap [i=" + i + "]";
        }

    }

    @Test
    public void shouldGetValuesWhenSubscribe() throws InterruptedException {
        for (int i = 0; i < 100; i++)
            connection.put(String.valueOf(i).getBytes());
        final IntWrap sum = new IntWrap();
        final IntWrap count = new IntWrap();
        final Subscription subscription = DriverManager.subscribe(url, (msg) -> {
            final byte[] next = msg.body();
            count.i++;
            sum.i += parseInt(new String(next));
        });
        Thread.sleep(10);
        subscription.close();
        assertThat(count.i, equalTo(100));
        assertThat(sum.i, equalTo(4950));
        assertThat(connection.get().isPresent(), equalTo(false));
    }

    @Test
    public void shouldGetValuesAfterSubscribe() throws InterruptedException {
        final IntWrap sum = new IntWrap();
        final IntWrap count = new IntWrap();
        final Subscription subscription = DriverManager.subscribe(url, (msg) -> {
            final byte[] next = msg.body();
            count.i++;
            sum.i += parseInt(new String(next));
        });
        for (int i = 0; i < 100; i++)
            connection.put(String.valueOf(i).getBytes());
        Thread.sleep(10);
        subscription.close();
        assertThat(count.i, equalTo(100));
        assertThat(sum.i, equalTo(4950));
        assertThat(connection.get().isPresent(), equalTo(false));
    }

}