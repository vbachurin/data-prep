package org.talend.dataprep.preparation.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.dataprep.api.preparation.PreparationRepository;
import org.talend.dataprep.preparation.store.InMemoryPreparationRepository;
import org.talend.dataprep.preparation.store.mongo.MongoDBPreparationRepository;

@Configuration
public class PreparationStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreparationStore.class);

    @Value("${preparation.store}")
    private String preparationStoreConfiguration;

    @Bean
    public PreparationRepository getVersionRepository() {
        LOGGER.info("Preparation store: {}", preparationStoreConfiguration);
        switch (preparationStoreConfiguration) {
        case "mongodb": //$NON-NLS-1$
            return new MongoDBPreparationRepository();
        case "in-memory": //$NON-NLS-1$
        default:
            return new InMemoryPreparationRepository();
        }
    }

}
