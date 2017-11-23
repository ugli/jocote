package se.ugli.jocote.stream.akka;

import akka.NotUsed;
import akka.stream.scaladsl.Sink;
import akka.stream.scaladsl.Source;
import scala.concurrent.ExecutionContext;
import se.ugli.jocote.Message;

public final class Jakka {

    private Jakka() {
    }

    public static Sink<Message, NotUsed> messageSink(final String url) {
        return Sink.fromSubscriber(new MessageSubscriber(url));
    }

    public static Source<Message, NotUsed> messageSource(final String url, final ExecutionContext executionContext) {
        return Source.fromPublisher(new MessagePublisher(url, executionContext));
    }

}
