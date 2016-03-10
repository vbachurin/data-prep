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

package org.talend.dataprep.api.service.command.dataset;

import static org.talend.dataprep.command.Defaults.asNull;

import org.apache.http.client.methods.HttpPut;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.command.GenericCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;

@Component
@Scope("request")
public class DatasetCertification extends GenericCommand<Void> {

    private DatasetCertification(String dataSetId) {
        super(GenericCommand.DATASET_GROUP);
        execute(() -> new HttpPut(datasetServiceUrl + "/datasets/" + dataSetId + "/processcertification"));
        onError(e -> new TDPException(APIErrorCodes.UNABLE_TO_CERTIFY_DATASET, e));
        on(HttpStatus.OK).then(asNull());
    }
}
