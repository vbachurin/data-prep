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

package org.talend.dataprep.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.talend.dataprep.api.preparation.PreparationActions;
import org.talend.dataprep.api.preparation.Step;
import org.talend.dataprep.api.service.info.VersionService;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

import java.io.IOException;
import java.util.Collections;

/**
 * Provide instance for root/initial content with current application version.
 */
@Configuration
public class BaseContent {

    /** The version service. */
    @Autowired
    private VersionService versionService;

    @Bean
    public Converter<String, JsonNode> jsonNodeConverter() {
        return new Converter<String, JsonNode>() {

            @Override
            public JsonNode convert(String source) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    return mapper.readTree(source);
                } catch (IOException e) {
                    throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
                }
            }
        };
    }

    /**
     * @return the preparation root content (no actions).
     */
    @Bean(name = "rootContent")
    public PreparationActions initRootContent() {
        return new PreparationActions(Collections.emptyList(), versionService.version().getVersionId());
    }

    /**
     * @return the default root step.
     */
    @Bean(name = "rootStep")
    public Step getRootStep(PreparationActions rootContent) {
        return new Step(null, rootContent.id(), versionService.version().getVersionId());
    }
}
