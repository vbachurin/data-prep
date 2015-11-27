package org.talend.dataprep.api.service.mail;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;

@Component @Scope("request") public class MailToCommand extends HystrixCommand<Void> {

    private final MailDetails mailDetails;

    public static final HystrixCommandGroupKey MAIL_GROUP = HystrixCommandGroupKey.Factory.asKey("mail"); //$NON-NLS-1$

    private MailToCommand(MailDetails mailDetails) {
        super(MAIL_GROUP);
        this.mailDetails = mailDetails;
    }

    @Override protected Void run() throws Exception {
        // TODO: Retrieve and send version with the feedback info object

        try {
            String body = "Version=0.10 BETA<br/>" + "Sender=" + mailDetails.getMail() + "<br/>" + "Type=" + mailDetails.getType()
                    + "<br/>" + "Severity=" + mailDetails.getSeverity() + "<br/>" + "Description=" + mailDetails.getDescription()
                    + "<br/>";

            MailSender.getInstance().send(mailDetails.getTitle(), body);
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_SEND_MAIL, e);
        }
        return null;
    }
}