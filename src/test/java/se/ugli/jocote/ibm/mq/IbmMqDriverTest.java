package se.ugli.jocote.ibm.mq;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.ugli.jocote.Connection;
import se.ugli.jocote.support.JocoteUrl;

public class IbmMqDriverTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void test() throws IOException {
        final IbmMqDriver driver = new IbmMqDriver();
        final Connection connection = driver.connect(JocoteUrl.apply("ibmmq:///QUEUE1?ConnectionNameList=192.168.232.1(1414)&QueueManager=QMA"));
        final Object object = connection.get();
        logger.info("{}", object);
        connection.close();
    }

}
