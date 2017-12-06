package se.ugli.jocote;

import static java.lang.Integer.parseInt;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import se.ugli.jocote.rabbitmq.RabbitMqProperties;

@RunWith(Parameterized.class)
public class DriverTest {

    private final String url;
    private final String testName;
    private Connection connection;

    public DriverTest(final String testName, final String url) {
        this.testName = testName;
        this.url = url;
    }

    @SuppressWarnings("rawtypes")
    @Parameterized.Parameters(name = "{0}")
    public static Collection testArray() {
        return Arrays.asList(new Object[][] { { "ActiveMQ", "activemq:/APA" }, { "RAM", "ram:/APA" },
                { "RabbitMQ", "rabbitmq:/APA" } });
    }

    @Before
    public void clearQueue() {
        try (Connection c = Jocote.connect(url)) {
            c.clear();
        }
        connection = Jocote.connect(url);
    }

    @After
    public void close() {
        connection.close();
        connection = null;
    }

    @Test
    public void shouldHandleGetAndPutBytes() throws InterruptedException, ExecutionException {
        assertThat(connection.get().isPresent(), equalTo(false));
        connection.put("hej".getBytes()).get();
        assertThat(new String(connection.get().get().body()), equalTo("hej"));
    }

    @Test
    public void shouldGetJmsHeaderValue() throws InterruptedException, ExecutionException {
        assumeFalse(testName.equals("RabbitMQ"));
        connection.put(Message.builder().body("hej".getBytes()).header("CorrelationID", "B").build()).get();
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
    public void shouldGetJmsPropertyValue() throws InterruptedException, ExecutionException {
        assumeFalse(testName.equals("RabbitMQ"));
        connection.put(Message.builder().body("hej".getBytes()).property("environment", "TEST").build()).get();
        assertThat(connection.get().get().properties().get("environment"), equalTo("TEST"));
    }

    @Test
    public void shouldGetRabbitMqCorrelationId() {
        assumeTrue(testName.equals("RabbitMQ"));
        connection
                .put(Message.builder().body("hej".getBytes()).property(RabbitMqProperties.CorrelationId, "B").build());
        assertThat(connection.get().get().properties().get(RabbitMqProperties.CorrelationId.name()), equalTo("B"));
    }

    @Test
    public void shouldGetRabbitMqDeliveryMode() {
        assumeTrue(testName.equals("RabbitMQ"));
        connection.put(Message.builder().body("hej".getBytes()).property(RabbitMqProperties.DeliveryMode, 2).build());
        assertThat(connection.get().get().properties().get(RabbitMqProperties.DeliveryMode.name()), equalTo(2));
    }

    @Test
    public void shouldCountMessages() throws InterruptedException {
        IntStream.rangeClosed(1, 200).mapToObj(i -> connection.put(String.valueOf(i).getBytes()))
                .map(CompletableFuture::join).count();

        Thread.sleep(200);
        assertThat(connection.messageCount(), equalTo(200L));
        assertThat(connection.messageCount(), equalTo(200L));
        assertThat(connection.get().isPresent(), equalTo(true));
    }

    @Test
    public void shouldConsumeStream() throws InterruptedException {
        IntStream.rangeClosed(0, 100).mapToObj(i -> connection.put(String.valueOf(i).getBytes())).map(CompletableFuture::join)
                .count();
        assertThat(connection.messageStream().mapToInt(m -> parseInt(new String(m.body()))).sum(), equalTo(5050));
        assertThat(connection.get().isPresent(), equalTo(false));
    }

    @Test
    public void shouldAcknoledgeStream() throws InterruptedException {
        IntStream.rangeClosed(0, 100).mapToObj(i -> connection.put(String.valueOf(i).getBytes())).map(CompletableFuture::join)
                .count();
        try (SessionStream stream = connection.sessionStream()) {
            assertThat(stream.mapToInt(m -> parseInt(new String(m.body()))).sum(), equalTo(5050));
            stream.ack();
        }
    }

    @Test
    public void shouldLimitMessageStream() throws InterruptedException {
        IntStream.rangeClosed(0, 100).mapToObj(i -> connection.put(String.valueOf(i).getBytes())).map(CompletableFuture::join)
                .count();
        assertThat(connection.messageStream().limit(10).mapToInt(m -> parseInt(new String(m.body()))).sum(),
                equalTo(45));
        assertThat(connection.messageStream().mapToInt(m -> parseInt(new String(m.body()))).sum(), equalTo(5005));
    }

    @Test
    public void shouldLeaveStream() throws InterruptedException {
        IntStream.range(0, 100).mapToObj(i -> connection.put(String.valueOf(i).getBytes())).map(CompletableFuture::join)
                .count();
        try (SessionStream stream = connection.sessionStream()) {
            assertThat(stream.mapToInt(m -> parseInt(new String(m.body()))).sum(), equalTo(4950));
            stream.nack();
        }
        assertThat(new String(connection.get().get().body()), equalTo("0"));
    }

    @Test
    public void shouldThrowThenNotLeavingOrAcknowledgeMessageStream() throws InterruptedException {
        IntStream.rangeClosed(0, 100).mapToObj(i -> connection.put(String.valueOf(i).getBytes())).map(CompletableFuture::join)
                .count();
        try (SessionStream stream = connection.sessionStream()) {
            assertThat(stream.mapToInt(m -> parseInt(new String(m.body()))).sum(), equalTo(5050));
        } catch (final JocoteException e) {
            assertThat(e.getMessage(), equalTo("You have to acknowledge or leave messages before closing"));
        }
    }

    @Test
    public void shouldGetValuesWhenSubscribe() throws InterruptedException {
        IntStream.range(0, 100).mapToObj(i -> connection.put(String.valueOf(i).getBytes())).map(CompletableFuture::join)
                .count();
        final IntWrap sum = new IntWrap();
        final IntWrap count = new IntWrap();
        final Subscription subscription = Jocote.subscribe(url, (msg) -> {
            final byte[] next = msg.body();
            count.i++;
            sum.i += parseInt(new String(next));
        });
        Thread.sleep(100);
        subscription.close();
        assertThat(count.i, equalTo(100));
        assertThat(sum.i, equalTo(4950));
        assertThat(connection.get().isPresent(), equalTo(false));
    }

    @Test
    public void shouldGetValuesAfterSubscribe() throws InterruptedException {
        final IntWrap sum = new IntWrap();
        final IntWrap count = new IntWrap();
        final Subscription subscription = Jocote.subscribe(url, (msg) -> {
            final byte[] next = msg.body();
            count.i++;
            sum.i += parseInt(new String(next));
        });
        IntStream.range(0, 100).mapToObj(i -> connection.put(String.valueOf(i).getBytes())).map(CompletableFuture::join)
                .count();
        Thread.sleep(10);
        subscription.close();
        assertThat(count.i, equalTo(100));
        assertThat(sum.i, equalTo(4950));
        assertThat(connection.get().isPresent(), equalTo(false));
    }
    


    private class IntWrap {
        int i = 0;

        @Override
        public String toString() {
            return "IntWrap [i=" + i + "]";
        }

    }

}