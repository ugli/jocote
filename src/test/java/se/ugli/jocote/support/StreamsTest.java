package se.ugli.jocote.support;

import org.junit.Test;
import se.ugli.jocote.Message;
import se.ugli.jocote.Message.MessageBuilder;

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
        assertThat(Streams.messageStream(iterator).limit(4).count(), is(batchSize));
        assertThat(iterator.index(), is((int)batchSize));
    }

}
