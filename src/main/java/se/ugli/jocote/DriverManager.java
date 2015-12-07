package se.ugli.jocote;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import se.ugli.jocote.support.JocoteUrl;

public final class DriverManager {

    private static final Map<String, Driver> drivers = new ConcurrentHashMap<String, Driver>();

    public static Connection getConnection(final String urlStr) {
        final JocoteUrl url = JocoteUrl.apply(urlStr);
        return getDriver(url).getConnection(url);
    }

    public static void register(final Driver driver) {
        final String urlScheme = driver.getUrlScheme();
        if (urlScheme == null)
            throw new JocoteException("Driver must define url scheme");
        drivers.put(urlScheme, driver);
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

    public static <T> Subscription<T> subscribe(final String urlStr, final Consumer<T> consumer) {
        final JocoteUrl url = JocoteUrl.apply(urlStr);
        return getDriver(url).subscribe(url, consumer);
    }

    private static Driver getDriver(final JocoteUrl url) {
        final Driver result = drivers.get(url.scheme);
        if (result == null)
            throw new IllegalStateException("No suitable driver for url: " + url);
        return result;
    }

    private DriverManager() {
    }

}
