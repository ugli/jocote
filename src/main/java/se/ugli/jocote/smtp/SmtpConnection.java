package se.ugli.jocote.smtp;

import static java.util.concurrent.CompletableFuture.runAsync;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.support.GetNotSupportedConnection;
import se.ugli.jocote.support.JocoteUrl;

public class SmtpConnection extends GetNotSupportedConnection {

    private final JocoteUrl url;
    private final Session session;

    public SmtpConnection(final JocoteUrl url) {
        this.url = url;
        final Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", url.host);
        if (url.port != null)
            properties.put("mail.smtp.port", String.valueOf(url.port));
        session = Session.getDefaultInstance(properties);
    }

    @Override
    public void close() {
    }

    @Override
    public CompletableFuture<Void> put(final Message msg) {
        return runAsync(() -> {
            try {
                final String from = (String) msg.headers().get("from");
                final String to = (String) msg.headers().get("to");
                final MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from));
                message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
                message.setSubject((String) msg.headers().get("subject"));
                message.setText(new String(msg.body()));
                Transport.send(message);
            } catch (final RuntimeException | MessagingException e) {
                throw new JocoteException(e);
            }
        }, executor());
    }

    @Override
    public String toString() {
        return url.toString();
    }

}
