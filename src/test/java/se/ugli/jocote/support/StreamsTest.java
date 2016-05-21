package se.ugli.jocote.support;

import org.junit.Test;
import se.ugli.jocote.Message;
import se.ugli.jocote.Message.MessageBuilder;
import se.ugli.jocote.MessageIterator;

import java.util.Arrays;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StreamsTest {

    @Test
    public void shouldLimitStream() {
        final long batchSize = 4;

        final MessageIterator iterator = new MessageIterator() {

            private final Queue<Message> queue = new LinkedBlockingQueue<>(
                    Arrays.asList(new MessageBuilder().build(), new MessageBuilder().build(), new MessageBuilder().build(),
                            new MessageBuilder().build(), new MessageBuilder().build(), new MessageBuilder().build(),
                            new MessageBuilder().build(), new MessageBuilder().build(), new MessageBuilder().build()));

            @Override
            public Optional<Message> next() {
                return Optional.ofNullable(queue.poll());
            }

        };
        assertThat(Streams.messageStream(iterator).limit(4).count(), is(batchSize));
    }

}
