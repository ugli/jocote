package se.ugli.jocote.lpr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/*
 * Provides an interface to LPD print servers running on network hosts
 * and network connected printers.  Conforms to RFC 1179.
 */

class Printer {

    final Socket socketLpr;
    final int jobNumber;
    final boolean printRaw;

    Printer(final Socket socketLpr, final int jobNumber, final boolean printRaw) {
        this.socketLpr = socketLpr;
        this.jobNumber = jobNumber;
        this.printRaw = printRaw;
    }

    public void printBytes(final byte[] bytes, final String hostName, final String printerName, final String documentName)
            throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(bytes);
        printStream(stream, hostName, printerName, documentName);
    }

    /*
     * Print a file to a network host or printer
     * fileName The path to the file to be printed
     * hostName The host name or IP address of the print server
     * printerName The name of the remote queue or the port on the print server
     * documentName The name of the document as displayed in the spooler of the host
     */
    public void printStream(final ByteArrayOutputStream stream, final String hostName, final String printerName, final String documentName)
            throws IOException {
        String controlFile = "";
        byte buffer[] = new byte[1000];
        String s;
        String strJobNumber;

        strJobNumber = "" + jobNumber;
        while (strJobNumber.length() < 3)
            strJobNumber = "0" + strJobNumber;

        String userName = System.getProperty("user.name");
        if (userName == null)
            userName = "Unknown";

        socketLpr.setSoTimeout(30000);
        final OutputStream sOut = socketLpr.getOutputStream();
        final InputStream sIn = socketLpr.getInputStream();

        //Open printer
        s = "\002" + printerName + "\n";
        sOut.write(s.getBytes());
        sOut.flush();
        acknowledge(sIn, "Failed to open printer");

        //Send control file
        controlFile += "H" + hostName + "\n";
        controlFile += "P" + userName + "\n";
        controlFile += (printRaw ? "o" : "p") + "dfA" + strJobNumber + hostName + "\n";
        controlFile += "UdfA" + strJobNumber + hostName + "\n";
        controlFile += "N" + documentName + "\n";

        s = "\002" + controlFile.length() + " cfA" + strJobNumber + hostName + "\n";
        sOut.write(s.getBytes());

        acknowledge(sIn, "Failed to send control header");

        buffer = controlFile.getBytes();
        sOut.write(buffer);
        buffer[0] = 0;
        sOut.write(buffer, 0, 1);
        sOut.flush();

        acknowledge(sIn, "Failed to send control file");

        s = "\003" + stream.size() + " dfA" + strJobNumber + hostName + "\n";
        sOut.write(s.getBytes());
        sOut.flush();
        acknowledge(sIn, "Failed to send print file command");

        stream.writeTo(sOut);
        sOut.flush();

        buffer[0] = 0;
        sOut.write(buffer, 0, 1);
        sOut.flush();
        acknowledge(sIn, "Failed to send print file");

        socketLpr.close();
    }

    /*
     * Print a file to a network host or printer
     * fileName The path to the file to be printed
     * hostName The host name or IP address of the print server
     * printerName The name of the remote queue or the port on the print server
     * documentName The name of the document as displayed in the spooler of the host
     */
    public void printFile(final File f, final String hostName, final String printerName, final String documentName) throws IOException {
        final byte buffer[] = new byte[1000];

        //Send print file
        if (!(f.exists() && f.isFile() && f.canRead()))
            throw new IOException("Error opening print file");
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (FileInputStream fs = new FileInputStream(f)) {
            int readCounter;
            do {
                readCounter = fs.read(buffer);
                if (readCounter > 0)
                    output.write(buffer, 0, readCounter);
            } while (readCounter > 0);
            printStream(output, hostName, printerName, documentName);
        }
    }

    private void acknowledge(final InputStream in, final String alert) throws IOException {
        if (in.read() != 0)
            throw new IOException(alert);
    }
}