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
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.api.action.ActionDefinition;
import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.service.api.DynamicParamsInput;
import org.talend.dataprep.metrics.Timed;

@Service(name = "dataprep.TransformAPI")
public interface TransformAPI {

    /**
     * Get all the possible actions for a given column.
     *
     * Although not rest compliant, this is done via a post in order to pass all the column metadata in the request body
     * without risking breaking the url size limit if GET would be used.
     *
     * @param columnMetadata the column description (json encoded) in the request body.
     */
    @RequestMapping(value = "/api/transform/actions/column", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    @Timed
    Stream<ActionDefinition> columnActions(ColumnMetadata columnMetadata);

    /**
     * Suggest the possible actions for a given column.
     *
     * Although not rest compliant, this is done via a post in order to pass all the column metadata in the request body
     * without risking breaking the url size limit if GET would be used.
     *
     * @param columnMetadata the column description (json encoded) in the request body.
     */
    @RequestMapping(value = "/api/transform/suggest/column", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    @Timed
    Stream<ActionDefinition> suggestColumnActions(ColumnMetadata columnMetadata);

    /**
     * Get all the possible actions available on lines.
     */
    @RequestMapping(value = "/api/transform/actions/line", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    Stream<ActionDefinition> lineActions();

    /**
     * Get the suggested action dynamic params. Dynamic params depends on the context (dataset / preparation / actual
     * transformations)
     */
    @RequestMapping(value = "/api/transform/suggest/{action}/params", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<StreamingResponseBody> suggestActionParams(@PathVariable("action") String action,
            @Valid DynamicParamsInput dynamicParamsInput);

    /**
     * Get the current dictionary (as serialized object).
     */
    @RequestMapping(value = "/api/transform/dictionary", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    StreamingResponseBody getDictionary();
}
