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

package org.talend.services.dataprep.api;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.api.service.mail.MailDetails;
import org.talend.dataprep.metrics.Timed;

@Service(name = "dataprep.MailServiceAPI")
public interface MailServiceAPI {

    /**
     * Send feedback to Talend
     *
     * @param mailDetails
     */
    @RequestMapping(value = "/api/mail", method = PUT)
    @Timed
    void mailTo(@RequestBody MailDetails mailDetails);
}
