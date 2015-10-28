package se.ugli.jocote;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DriverManager {

	private static Map<Class<?>, Driver> drivers = new ConcurrentHashMap<Class<?>, Driver>();

	public static Connection getConnection(final String url) {
		for (final Driver driver : drivers.values())
			if (driver.acceptsURL(url))
				return driver.getConnection(url);
		throw new IllegalStateException("No suitable driver for url: " + url);
	}

	public static void register(final Driver driver) {
		drivers.put(driver.getClass(), driver);
	}

	private DriverManager() {
	}

}
