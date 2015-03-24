package org.talend.dataprep.api.preparation.configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.dataprep.api.preparation.PreparationRepository;
import org.talend.dataprep.api.preparation.store.ContentCache;
import org.talend.dataprep.api.preparation.store.InMemoryPreparationRepository;

@Configuration
public class PreparationStore {

    private static final Log LOGGER = LogFactory.getLog(PreparationStore.class);

    @Bean
    public ContentCache getContentCache() {
        return new ContentCache() {
            @Override
            public boolean has(String preparationId, String stepId) {
                return false;
            }

            @Override
            public InputStream get(String preparationId, String stepId) {
                return new ByteArrayInputStream(new byte[0]);
            }
        };
    }

    @Bean
    public PreparationRepository getVersionRepository() {
        LOGGER.info("Using in-memory version store.");
        return new InMemoryPreparationRepository();
    }

}
