package se.ugli.jocote.support;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

import se.ugli.jocote.Iterator;
import se.ugli.jocote.Message;
import se.ugli.jocote.Message.MessageBuilder;

public class StreamsTest {

    @Test
    public void shouldLimitStream() {
        final int batchSize = 4;

        final Iterator<Message> iterator = new Iterator<Message>() {

            private final Queue<Message> queue = new LinkedBlockingQueue<>(
                    Arrays.asList(new MessageBuilder().build(), new MessageBuilder().build(), new MessageBuilder().build(),
                            new MessageBuilder().build(), new MessageBuilder().build(), new MessageBuilder().build(),
                            new MessageBuilder().build(), new MessageBuilder().build(), new MessageBuilder().build()));

            @Override
            public Optional<Message> next() {
                return Optional.ofNullable(queue.poll());
            }
        };
        final long count = Streams.stream(iterator, batchSize).count();
        assertThat((int) count, is(batchSize));
    }

}
