package se.ugli.jocote;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.ugli.jocote.support.JocoteProperties;
import se.ugli.jocote.support.JocoteUrl;
import se.ugli.jocote.support.DebugLogConnectionProxy;

public final class Jocote {

    private final static Logger logger = LoggerFactory.getLogger(Jocote.class);
    private static final Map<String, Driver> drivers = new ConcurrentHashMap<>();

    static {
        tryToRegister("se.ugli.jocote.activemq.ActiveMqDriver");
        tryToRegister("se.ugli.jocote.ibm.mq.IbmMqDriver");
        tryToRegister("se.ugli.jocote.jndi.JndiDriver");
        tryToRegister("se.ugli.jocote.ram.RamDriver");
        tryToRegister("se.ugli.jocote.rabbitmq.RabbitMqDriver");
        tryToRegister("se.ugli.jocote.log.LogDriver");
        tryToRegister("se.ugli.jocote.smtp.SmtpDriver");
    }

    private static void tryToRegister(final String driver) {
        try {
            register(driver);
            logger.info("Driver {} registered.", driver);
        }
        catch (final JocoteException e) {
            logger.error("Driver {} not registered: {}", driver, e.getMessage());
        }
    }

    public static Connection connect(final String url) {
        final JocoteUrl urlObj = JocoteUrl.apply(url);
        final Connection connection = driver(urlObj).connect(urlObj);
        if (JocoteProperties.debugLogConnections())
            return new DebugLogConnectionProxy(connection);
        return connection;
    }

    public static void register(final Driver driver) {
        final String urlScheme = driver.urlScheme();
        if (urlScheme == null)
            throw new JocoteException("Driver must define url scheme");
        drivers.put(urlScheme, driver);
    }

    public static void register(final String driver) {
        try {
            register((Driver) Class.forName(driver).newInstance());
        }
        catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new JocoteException(e);
        }
    }

    public static Subscription subscribe(final String url, final Consumer<Message> consumer) {
        final JocoteUrl urlObj = JocoteUrl.apply(url);
        return driver(urlObj).subscribe(urlObj, consumer);
    }

    private static Driver driver(final JocoteUrl url) {
        final Driver result = drivers.get(url.scheme);
        if (result == null)
            throw new JocoteException("No suitable driver for url: " + url);
        return result;
    }

    private Jocote() {
    }

}
