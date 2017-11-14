package se.ugli.jocote;

import java.io.Closeable;

public interface ConnectionPool extends Closeable {

    Connection connection();

    @Override
    void close();

}
