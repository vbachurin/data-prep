package org.talend.dataprep.api.service.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;

/**
 * Utility class to send mail.
 */
public class MailSender {

    private static Logger log = Logger.getLogger(MailSender.class.getSimpleName());

    private static MailSender singleton;

    private MailSender() {
    }

    public static MailSender getInstance() {
        if (singleton == null)
            singleton = new MailSender();
        return singleton;
    }

    public void send(String subject, String body) {

        send(subject, body, Configuration.INSTANCE.getAsString("mail.smtp.to"));
    }

    public void send(String subject, String body, String recipients) {
        try {
            // ==============================================
            // Assure unicity in recipients list:
            // ==============================================
            String[] split = recipients.split(",");
            recipients = split.length > 0 ? split[0] : "";
            for (int i = 1; i < split.length; i++) {
                if (!recipients.contains(split[i])) {
                    recipients += "," + split[i];
                }
            }
            // ==============================================

            subject = Configuration.INSTANCE.getAsString("mail.smtp.subject.prefix") + subject;
            body = Configuration.INSTANCE.getAsString("mail.smtp.body.prefix") + "<br/><br/>" + body + "<br/><br/>"
                    + Configuration.INSTANCE.getAsString("mail.smtp.body.suffix");

            String name = Configuration.INSTANCE.getAsString("mail.smtp.username");
            String pwd = Configuration.INSTANCE.getAsString("mail.smtp.password");

            InternetAddress from = new InternetAddress(Configuration.INSTANCE.getAsString("mail.smtp.from"));
            InternetAddress replyTo = new InternetAddress(Configuration.INSTANCE.getAsString("mail.smtp.reply"));
            InternetAddress copy = new InternetAddress(Configuration.INSTANCE.getAsString("mail.smtp.copy"));

            if (Configuration.INSTANCE.getAsBoolean("debug")) {
                System.out.println("***** Sending mail");
                System.out.println("from: " + from.toString());
                System.out.println("username: " + name);
                System.out.println("Recipients=" + recipients);
                System.out.println("Subject=" + subject);
                System.out.println("Body=" + body);
            }
            Properties p = new Properties();
            p.put("mail.smtp.host", Configuration.INSTANCE.getAsString("mail.smtp.host"));
            p.put("mail.smtp.port", Configuration.INSTANCE.getAsString("mail.smtp.port"));
            p.put("mail.smtp.starttls.enable","true");
            p.put("mail.smtp.auth", "true");

            MailAuthenticator authenticator = new MailAuthenticator(name, pwd);
            Session sendMailSession = Session.getInstance(p, authenticator);

            MimeMessage msg = new MimeMessage(sendMailSession);
            msg.setFrom(from);
            msg.setReplyTo(new Address[] { replyTo });
            msg.addRecipients(Message.RecipientType.TO, recipients);
            msg.addRecipient(Message.RecipientType.BCC, copy);

            msg.setSubject(subject, "UTF-8");
            msg.setSentDate(new Date());
            Multipart mainPart = new MimeMultipart();
            BodyPart html = new MimeBodyPart();
            html.setContent(body, "text/html; charset=utf-8");
            mainPart.addBodyPart(html);
            msg.setContent(mainPart);
            Transport.send(msg);

            log.debug("Sending mail:'" + subject + "' to '" + Configuration.INSTANCE.getAsString("mail.smtp.to") + "'");
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_SEND_MAIL, e);
        }
    }

    public static class MailAuthenticator extends Authenticator {

        String userName = null;

        String password = null;

        public MailAuthenticator() {
        }

        public MailAuthenticator(String username, String password) {
            this.userName = username;
            this.password = password;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, password);
        }
    }

}
