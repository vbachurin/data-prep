package org.talend.dataprep.dataset.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.dataset.store.local.InMemoryDataSetMetadataRepository;
import org.talend.dataprep.dataset.store.mongo.MongoDataSetMetadataRepository;

@Configuration
public class DataSetStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSetStore.class);

    @Value("${dataset.metadata.store}")
    private String metadataStoreConfiguration;

    @Bean
    public DataSetMetadataRepository getStore() {
        LOGGER.info("Data Set metadata store: {}", metadataStoreConfiguration);
        switch (metadataStoreConfiguration) {
        case "mongodb": //$NON-NLS-1$
            return new MongoDataSetMetadataRepository();
        case "in-memory": //$NON-NLS-1$
        default:
            return new InMemoryDataSetMetadataRepository();
        }
    }
}
