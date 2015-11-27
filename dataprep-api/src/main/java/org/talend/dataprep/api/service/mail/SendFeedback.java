package org.talend.dataprep.api.service.mail;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.service.APIService;

@Component @Scope("request") public class SendFeedback extends HystrixCommand<Void> {

    private final FeedbackInfo feedbackInfo;

    public static final HystrixCommandGroupKey MAIL_GROUP = HystrixCommandGroupKey.Factory.asKey("mail"); //$NON-NLS-1$

    private SendFeedback(FeedbackInfo feedbackInfo) {
        super(MAIL_GROUP);
        this.feedbackInfo = feedbackInfo;
    }

    @Override protected Void run() throws Exception {
        System.out.println("feedbackInfo = " + feedbackInfo);
        //TODO: Retrieve and send version with the feedback info object
        return null;
    }
}