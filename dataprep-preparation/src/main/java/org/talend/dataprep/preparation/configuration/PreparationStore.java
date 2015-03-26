package org.talend.dataprep.preparation.configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.dataprep.api.preparation.PreparationRepository;
import org.talend.dataprep.preparation.store.ContentCache;
import org.talend.dataprep.preparation.store.InMemoryPreparationRepository;
import org.talend.dataprep.preparation.store.mongo.MongoDBPreparationRepository;

@Configuration
public class PreparationStore {

    private static final Log LOGGER = LogFactory.getLog(PreparationStore.class);

    @Value("${preparation.store}")
    private String preparationStoreConfiguration;

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
        LOGGER.info("Preparation store: " + preparationStoreConfiguration);
        switch (preparationStoreConfiguration) {
            case "mongodb": //$NON-NLS-1$
                return new MongoDBPreparationRepository();
            case "in-memory": //$NON-NLS-1$
            default:
                return new InMemoryPreparationRepository();
        }
    }

}
