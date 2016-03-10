//  ============================================================================
//
//  Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  https://github.com/Talend/data-prep/blob/master/LICENSE
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================

package org.talend.dataprep.api.service.command.preparation;

import static org.talend.dataprep.command.Defaults.asNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.AppendStep;
import org.talend.dataprep.api.preparation.StepDiff;
import org.talend.dataprep.api.service.command.common.ChainedCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

@Component
@Scope("request")
public class PreparationAddAction extends ChainedCommand<Void, InputStream> {

    /**
     * Default constructor.
     *
     * @param preparationId the preparation id.
     * @param step the step to append.
     * @param diffInput the command to execute to get the diff metadata of the steps to append.
     */
    // private constructor to ensure the use of IoC
    private PreparationAddAction(final String preparationId, final AppendStep step, DiffMetadata diffInput) {
        super(PREPARATION_GROUP, diffInput);
        execute(() -> onExecute(preparationId, step));
        on(HttpStatus.OK).then(asNull());
    }


    private HttpRequestBase onExecute(String preparationId, AppendStep step) {
        try {
            final StepDiff diff = objectMapper.readValue(getInput(), StepDiff.class);
            step.setDiff(diff);
            final HttpPost actionAppend = new HttpPost(preparationServiceUrl + "/preparations/" + preparationId + "/actions");
            final String stepAsString = objectMapper.writeValueAsString(step);
            final InputStream stepInputStream = new ByteArrayInputStream(stepAsString.getBytes());
            actionAppend.setHeader(new BasicHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));
            actionAppend.setEntity(new InputStreamEntity(stepInputStream));
            return actionAppend;
        } catch (IOException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }
}
