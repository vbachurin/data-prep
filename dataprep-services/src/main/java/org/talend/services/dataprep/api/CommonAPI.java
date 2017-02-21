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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.api.type.Type;
import org.talend.dataprep.metrics.Timed;

/**
 * Common API that does not stand in either DataSet, Preparation nor Transform.
 */
@Service(name = "dataprep.CommonAPI")
public interface CommonAPI {

    /**
     * Describe the supported error codes.
     *
     * @param output the http response.
     */
    @RequestMapping(value = "/api/errors", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    void listErrors(OutputStream output) throws IOException;

    /**
     * Describe the supported Types
     *
     */
    @RequestMapping(value = "/api/types", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Type[] listTypes() throws IOException;
}
