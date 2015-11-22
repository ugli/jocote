package se.ugli.jocote;

import java.util.Set;

public interface MessageContext {

    String getMessageId();

    Set<String> getHeaderNames();

    <T> T getHeader(String headerName);

    Set<String> getPropertyNames();

    <T> T getProperty(String propertyName);

}
