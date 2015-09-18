// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprep.api.service.command.dataset;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.APIErrorCodes;
import org.talend.dataprep.api.service.PreparationAPI;
import org.talend.dataprep.api.service.command.common.DataPrepCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.daikon.exception.ExceptionContext;

/**
 * API command to call the dataset favorite api
 *
 */
@Component
@Scope("request")
public class SetFavoritesCmd extends DataPrepCommand<String> {

    private String dataSetId;

    private boolean unset;

    private SetFavoritesCmd(HttpClient client, String dataSetId, boolean unset) {
        super(PreparationAPI.DATASET_GROUP, client);
        this.dataSetId = dataSetId;
        this.unset = unset;
    }

    @Override
    protected String run() throws Exception {
        HttpPut contentRetrieval = new HttpPut(datasetServiceUrl + "/datasets/" + dataSetId + "/favorite?unset=" + unset);
        HttpResponse response = client.execute(contentRetrieval);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200) {
            return null;
        }
        throw new TDPException(APIErrorCodes.UNABLE_TO_SET_FAVORITE_DATASET, ExceptionContext.build().put("id", dataSetId));
    }

}
