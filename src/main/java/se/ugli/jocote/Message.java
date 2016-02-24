package se.ugli.jocote;

import java.util.Set;

public interface Message {

    byte[] body();

    String id();

    Set<String> headerNames();

    <T> T header(String headerName);

    Set<String> propertyNames();

    <T> T property(String propertyName);

}
