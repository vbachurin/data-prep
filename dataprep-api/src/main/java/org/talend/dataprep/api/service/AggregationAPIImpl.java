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

package org.talend.dataprep.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.talend.daikon.annotation.Client;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.transformation.aggregation.api.AggregationParameters;
import org.talend.dataprep.transformation.aggregation.api.AggregationResult;
import org.talend.services.dataprep.TransformationService;
import org.talend.services.dataprep.api.AggregationAPI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ServiceImplementation
public class AggregationAPIImpl extends APIService implements AggregationAPI {

    @Client
    TransformationService transformationService;

    @Autowired
    ObjectMapper mapper;

    @Override
    public AggregationResult compute(AggregationParameters input) {
        LOG.debug("Aggregation computation requested (pool: {} )...", getConnectionStats());
        try {
            return transformationService.aggregate(mapper.writer().writeValueAsString(input));
        } catch (JsonProcessingException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PARSE_JSON, e);
        } finally {
            LOG.debug("Aggregation done (pool: {} )...", getConnectionStats());
        }
    }

}
