package se.ugli.jocote.ibm.mq;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class IbmMqUrlTest {

    @Test
    public void shouldGiveQueueWithoutParams() {
        assertThat(new IbmMqUrl("jms:ibmmq:queue@AbC").queue, equalTo("AbC"));
    }

    @Test
    public void shouldGiveQueueWithParams() {
        assertThat(new IbmMqUrl("jms:ibmmq:queue@AbC?TransportType=1").queue, equalTo("AbC"));
    }

    @Test
    public void shouldHaveOneParam() {
        assertThat(new IbmMqUrl("jms:ibmmq:queue@AbC?TransportType=1").params.get("TransportType"), equalTo("1"));
    }

    @Test
    public void shouldHaveTowParams() {
        final IbmMqUrl url = new IbmMqUrl("jms:ibmmq:queue@AbC?TransportType=1&QueueManager=Test");
        assertThat(url.params.get("TransportType"), equalTo("1"));
        assertThat(url.params.get("QueueManager"), equalTo("Test"));
    }

}
