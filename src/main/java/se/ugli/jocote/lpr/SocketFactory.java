package se.ugli.jocote.lpr;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.stream.IntStream;

class SocketFactory {


    /*
     * The RFC for lpr specifies the use of local ports numbered 721 - 731, however
     * TCP/IP also requires that any port that is used will not be released for 3 minutes
     * which means that it get stuck on the 12th job if prints are sent quickly.
     *
     * To resolve this issue you can use out of bounds ports which most print servers
     * will support
     *
     */
    static Socket create(String hostName, int port, boolean useOutOfBoundsPorts) throws IOException {
        if (useOutOfBoundsPorts) {
            return IntStream.rangeClosed(721, 731).mapToObj(localPort -> {
                try {
                    return Optional.of(new Socket(hostName, port, InetAddress.getLocalHost(), localPort));
                } catch (IOException e) {
                    return Optional.<Socket>empty();
                }
            }).filter(Optional::isPresent).map(Optional::get).findFirst().orElseThrow(() -> new BindException("Can't bind to local port/address"));
        }
        return new Socket(hostName, port);
    }

}
