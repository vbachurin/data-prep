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
import static org.talend.dataprep.command.Defaults.convertResponse;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.dataset.DataSet;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.command.GenericCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;
import org.talend.dataprep.exception.error.CommonErrorCodes;

import javax.annotation.PostConstruct;

@Component
@Scope("prototype")
public class DataSetSampleGetMetadata extends GenericCommand<DataSetMetadata> {

    /**
     * Private constructor to ensure the use of IoC
     * @param dataSetId the dataset id to get.
     */
    private DataSetSampleGetMetadata(String dataSetId) {
        super(GenericCommand.DATASET_GROUP);
        execute(() -> new HttpGet(datasetServiceUrl + "/datasets/" + dataSetId + "/sample/metadata"));
        onError(e -> new TDPException(APIErrorCodes.UNABLE_TO_RETRIEVE_DATASET_METADATA, e));
        on(HttpStatus.NO_CONTENT).then(asNull());
    }

    @PostConstruct
    public void init() {
        on(HttpStatus.OK).then(convertResponse(objectMapper, DataSetMetadata.class));
    }

}
