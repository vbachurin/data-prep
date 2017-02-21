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

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.transformation.aggregation.api.AggregationParameters;
import org.talend.dataprep.transformation.aggregation.api.AggregationResult;

/**
 * High level Aggregation API.
 */
@Service(name = "dataprep.AggregationAPI")
public interface AggregationAPI {

    /**
     * Compute an aggregation according to the given parameters.
     *
     * @param input The aggregation parameters.
     */
    @RequestMapping(value = "/api/aggregate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    AggregationResult compute(@RequestBody @Valid AggregationParameters input);
}
