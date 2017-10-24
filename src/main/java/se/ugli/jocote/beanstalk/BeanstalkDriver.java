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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Connection connect(JocoteUrl url) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Subscription subscribe(JocoteUrl url, Consumer<Message> consumer) {
        // TODO Auto-generated method stub
        return null;
    }

}
