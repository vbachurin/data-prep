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

package org.talend.dataprep.api.service.command.folder;

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

import java.io.InputStream;
import java.net.URISyntaxException;

import static org.talend.dataprep.command.Defaults.pipeStream;

/**
 * List sub folders for the given path.
 */
@Component
@Scope("prototype")
public class FolderChildrenList extends GenericCommand<InputStream> {

    /**
     * List folder's children.
     *
     * @param parentId the parent folder id.
     * @param sort     the sort key.
     * @param order    how to use the sort key.
     */
    // private constructor to ensure IoC
    private FolderChildrenList(String parentId, String sort, String order) {
        super(GenericCommand.PREPARATION_GROUP);
        execute(() -> onExecute(parentId, sort, order));
        on(HttpStatus.OK).then(pipeStream());
    }

    /**
     * List folder's children.
     *
     * @param parentId the parent folder id.
     */
    // private constructor to ensure IoC
    private FolderChildrenList(String parentId) {
        this(parentId, null, null);
    }

    private HttpRequestBase onExecute(final String parentId, final String sort, final String order) {
        try {
            final URIBuilder uriBuilder = new URIBuilder(preparationServiceUrl + "/folders/" + parentId + "/children");

            if (StringUtils.isNotBlank(sort)) {
                uriBuilder.addParameter("sort", sort);
            }
            if (StringUtils.isNotBlank(order)) {
                uriBuilder.addParameter("order", order);
            }

            return new HttpGet(uriBuilder.build());

        } catch (URISyntaxException e) {
            throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }

}
