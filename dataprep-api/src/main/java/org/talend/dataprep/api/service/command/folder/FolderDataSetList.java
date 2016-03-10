// ============================================================================
//
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

package org.talend.dataprep.api.service.command.folder;


import static org.talend.dataprep.command.Defaults.pipeStream;

import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.talend.dataprep.command.GenericCommand;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

@Component
@Scope("request")
public class FolderDataSetList extends GenericCommand<InputStream> {

    public FolderDataSetList(String sort, String order, String folder) {
        super(GenericCommand.TRANSFORM_GROUP);
        execute(() -> onExecute(sort, order, folder));
        on(HttpStatus.OK).then(pipeStream());
    }

    private HttpRequestBase onExecute(String sort, String order, String folder) {
        try {
            URIBuilder uriBuilder = new URIBuilder(datasetServiceUrl + "/folders/datasets");
            uriBuilder.addParameter("sort", sort);
            uriBuilder.addParameter("order", order);
            if (StringUtils.isNotEmpty(folder)) {
                uriBuilder.addParameter("folder", folder);
            }
            return new HttpGet(uriBuilder.build());
        } catch (URISyntaxException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

}
