package se.ugli.jocote.lpr;

import static java.util.concurrent.CompletableFuture.runAsync;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.support.GetNotSupportedConnection;
import se.ugli.jocote.support.JocoteUrl;

class LprConnection extends GetNotSupportedConnection {

    private final JocoteUrl url;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Socket socket;

    /*
     * By default prints all files as raw binary data. Set this value to false
     * to use the text formatting of the spooler on the host.
     */
    private final boolean printRaw;
    private final AtomicInteger jobNumber = new AtomicInteger(0);
    private final String hostName;
    private final String printerName;

    LprConnection(final JocoteUrl url) {
        try {
            this.url = url;
            hostName = host(url);
            printRaw = printRaw(url);
            printerName = url.queue;
            socket = SocketFactory.create(hostName, port(url), useOutOfBoundsPorts(url));
        } catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    private String host(final JocoteUrl url) {
        if (url.host != null)
            return url.host;
        return "127.0.0.1";
    }

    private boolean printRaw(final JocoteUrl url) {
        final String param = url.params.get("printRaw");
        if (param == null || "true".equals(param))
            return true;
        else if ("false".equals(param))
            return false;
        return true;
    }

    private boolean useOutOfBoundsPorts(final JocoteUrl url) {
        return "true".equals(url.params.get("useOutOfBoundsPorts"));
    }

    private int port(final JocoteUrl url) {
        if (url.port != null)
            return url.port;
        return 515;
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (final RuntimeException | IOException e) {
            logger.warn("Couldn't close connection: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<Void> put(final Message message) {
        return runAsync(() -> {
            try {
                // Job number cycles from 001 to 999
                if (jobNumber.incrementAndGet() >= 1000)
                    jobNumber.set(1);
                final Printer printer = new Printer(socket, jobNumber.get(), printRaw);
                final Object fileObj = message.properties().get("file");
                if (fileObj == null)
                    printer.printBytes(message.body(), hostName, printerName,
                            message.properties().getOrDefault("documentName", "untitled").toString());
                else {
                    File file;
                    if (fileObj instanceof String)
                        file = new File((String) fileObj);
                    else if (fileObj instanceof File)
                        file = (File) fileObj;
                    else if (fileObj instanceof Path)
                        file = ((Path) fileObj).toFile();
                    else
                        throw new IllegalArgumentException(fileObj.getClass().getName());
                    final String documentName = message.properties().getOrDefault("documentName", file.getName())
                            .toString();
                    printer.printFile(file, hostName, printerName, documentName);
                }
            } catch (final IOException | RuntimeException e) {
                throw new JocoteException(e);
            }
        }, executor());
    }

    @Override
    public String toString() {
        return url.toString();
    }

}
