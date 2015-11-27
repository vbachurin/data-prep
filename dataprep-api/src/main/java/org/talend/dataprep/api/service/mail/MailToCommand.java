package org.talend.dataprep.api.service.mail;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component @Scope("request") public class MailToCommand extends HystrixCommand<Void> {

    private final MailDetails mailDetails;

    public static final HystrixCommandGroupKey MAIL_GROUP = HystrixCommandGroupKey.Factory.asKey("mail"); //$NON-NLS-1$

    private MailToCommand(MailDetails mailDetails) {
        super(MAIL_GROUP);
        this.mailDetails = mailDetails;
    }

    @Override
    protected Void run() throws Exception {
        // TODO: Retrieve and send version with the feedback info object

        String body = "Version=0.10 BETA<br/>";
        body += "Sender=" + mailDetails.getMail() + "<br/>";
        body += "Type=" + mailDetails.getType() + "<br/>";
        body += "Severity=" + mailDetails.getSeverity() + "<br/>";
        body += "Description=" + mailDetails.getDescription() + "<br/>";

        MailSender.getInstance().send(mailDetails.getTitle(), body);
        return null;
    }
}