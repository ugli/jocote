package se.ugli.jocote.beanstalk;

import static java.util.Optional.empty;

import java.util.Optional;

import com.surftools.BeanstalkClient.Job;
import com.surftools.BeanstalkClientImpl.ClientImpl;

import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;

class BeanstalkMessageIterator implements MessageIterator {

    private final ClientImpl client;
    private final Optional<Integer> reserveTimeoutSeconds;

    BeanstalkMessageIterator(ClientImpl client, Optional<Integer> reserveTimeoutSeconds) {
        this.client = client;
        this.reserveTimeoutSeconds = reserveTimeoutSeconds;
    }

    @Override
    public Optional<Message> next() {
        final Job job = client.reserve(reserveTimeoutSeconds.orElse(null));
        if (job != null) {
            final String id = String.valueOf(job.getJobId());
            final byte[] body = job.getData();
            return Optional.of(Message.builder().id(id).body(body).build());
        }
        return empty();
    }

}
