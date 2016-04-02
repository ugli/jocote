package se.ugli.jocote.support;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

import se.ugli.jocote.Message;
import se.ugli.jocote.Message.MessageBuilder;

public class StreamsTest {

    @Test
    public void shouldLimitStream() {
        final int batchSize = 4;

        final MessageIterator iterator = new MessageIterator() {

            private final Queue<Message> queue = new LinkedBlockingQueue<>(
                    Arrays.asList(new MessageBuilder().build(), new MessageBuilder().build(), new MessageBuilder().build(),
                            new MessageBuilder().build(), new MessageBuilder().build(), new MessageBuilder().build(),
                            new MessageBuilder().build(), new MessageBuilder().build(), new MessageBuilder().build()));
            private int consumed;

            @Override
            public Optional<Message> next() {
                final Optional<Message> result = Optional.ofNullable(queue.poll());
                if (result.isPresent())
                    consumed++;
                return result;
            }

            @Override
            public int index() {
                return consumed;
            }
        };
        final long count = Streams.messageStream(iterator, batchSize).count();
        assertThat((int) count, is(batchSize));
    }

}
