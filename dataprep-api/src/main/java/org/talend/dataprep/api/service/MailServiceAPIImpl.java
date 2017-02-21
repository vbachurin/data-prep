// ============================================================================
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.api.service;

import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.dataprep.api.service.mail.MailDetails;
import org.talend.dataprep.api.service.mail.MailToCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;
import org.talend.services.dataprep.api.MailServiceAPI;

import com.netflix.hystrix.HystrixCommand;

@ServiceImplementation
public class MailServiceAPIImpl extends APIService implements MailServiceAPI {

    @Override
    public void mailTo(MailDetails mailDetails) {
        if (mailDetails.isEmpty()) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_GET_MAIL_DETAILS);
        }
        try {

            final HystrixCommand<Void> sendFeedback = getCommand(MailToCommand.class, mailDetails);
            sendFeedback.execute();

        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_SEND_MAIL, e);
        }

    }

}
