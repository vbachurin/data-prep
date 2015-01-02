package org.talend.dataprep.dataset.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.dataset.store.local.InMemoryDataSetMetadataRepository;
import org.talend.dataprep.dataset.store.local.LocalDataSetContentStore;
import org.talend.dataprep.dataset.store.mongo.MongoDataSetMetadataRepository;

@Configuration
public class DataSetStoreConfiguration {

    private static final Log LOGGER = LogFactory.getLog(DataSetStoreConfiguration.class);

    @Value("${dataset.metadata.store}")
    private String           metadataStoreConfiguration;

    @Bean
    public DataSetContentStore getContentStore() {
        return new LocalDataSetContentStore();
    }

    @Bean
    public DataSetMetadataRepository getStore() {
        LOGGER.info("Data Set metadata store: " + metadataStoreConfiguration);
        if ("mongodb".equalsIgnoreCase(metadataStoreConfiguration)) {
            return new MongoDataSetMetadataRepository();
        } else if ("in-memory".equalsIgnoreCase(metadataStoreConfiguration)) {
            return new InMemoryDataSetMetadataRepository();
        } else {
            return new InMemoryDataSetMetadataRepository();
        }
    }
}
