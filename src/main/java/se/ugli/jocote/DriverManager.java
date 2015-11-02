package se.ugli.jocote;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DriverManager {

    private static Map<Class<?>, Driver> drivers = new ConcurrentHashMap<Class<?>, Driver>();

    public static Connection getConnection(final String url) {
        return getDriver(url).getQueueConnection(url);
    }

    public static void register(final Driver driver) {
        drivers.put(driver.getClass(), driver);
    }

    public static void register(final String driver) {
        try {
            register((Driver) Class.forName(driver).newInstance());
        }
        catch (final InstantiationException e) {
            throw new JocoteException(e);
        }
        catch (final IllegalAccessException e) {
            throw new JocoteException(e);
        }
        catch (final ClassNotFoundException e) {
            throw new JocoteException(e);
        }
    }

    public static <T> Subscription<T> subscribe(final String url, final Consumer<T> consumer) {
        return getDriver(url).subscribe(url, consumer);
    }

    private static Driver getDriver(final String url) {
        for (final Driver driver : drivers.values())
            if (driver.acceptsURL(url))
                return driver;
        throw new IllegalStateException("No suitable driver for url: " + url);
    }

    private DriverManager() {
    }

}
