package se.ugli.jocote.beanstalk;

import java.util.function.Consumer;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Driver;
import se.ugli.jocote.Message;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.JocoteUrl;

public class BeanstalkDriver implements Driver {

    @Override
    public String urlScheme() {
        return "beanstalk";
    }

    @Override
    public Connection connect(JocoteUrl url) {
        return new BeanstalkConnection(url);
    }

    @Override
    public Subscription subscribe(JocoteUrl url, Consumer<Message> consumer) {
        throw new UnsupportedOperationException("Beanstalk has no subscription features");
    }

}
