package se.ugli.jocote.activemq;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import se.ugli.jocote.support.JocoteUrl;

public class ActiveMqUrlTest {

    @Test
    public void shouldHandleOneConnectionComponentsUrl() {
        final JocoteUrl url = JocoteUrl.apply("activemq:/APA");
        assertThat(url.scheme, equalTo("activemq"));
        assertThat(url.host, equalTo(null));
        assertThat(url.port, equalTo(null));
        assertThat(url.queue, equalTo("APA"));
    }

    @Test
    public void shouldHandleTwoConnectionComponentsUrl() {
        final JocoteUrl url = JocoteUrl.apply("activemq://kth.se/APA");
        assertThat(url.scheme, equalTo("activemq"));
        assertThat(url.host, equalTo("kth.se"));
        assertThat(url.port, equalTo(null));
        assertThat(url.queue, equalTo("APA"));
    }

    @Test
    public void shouldHandleThreeConnectionComponentsUrl() {
        final JocoteUrl url = JocoteUrl.apply("activemq://kth.se:666/APA");
        assertThat(url.scheme, equalTo("activemq"));
        assertThat(url.host, equalTo("kth.se"));
        assertThat(url.port, equalTo(666));
        assertThat(url.queue, equalTo("APA"));
    }

}
