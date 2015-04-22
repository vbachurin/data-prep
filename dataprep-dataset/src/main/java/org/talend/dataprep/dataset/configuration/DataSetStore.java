package org.talend.dataprep.dataset.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.dataset.store.hdfs.HDFSContentStore;
import org.talend.dataprep.dataset.store.local.InMemoryDataSetMetadataRepository;
import org.talend.dataprep.dataset.store.local.LocalDataSetContentStore;
import org.talend.dataprep.dataset.store.mongo.MongoDataSetMetadataRepository;

@Configuration
public class DataSetStore implements EnvironmentAware {

    private static final Logger LOGGER = LoggerFactory.getLogger( DataSetStore.class );

    @Value("${dataset.metadata.store}")
    private String metadataStoreConfiguration;

    @Value("${dataset.content.store}")
    private String contentStoreConfiguration;

    private Environment environment;

    @Bean
    public DataSetContentStore getContentStore() {
        LOGGER.info("Data Set content store: {}", contentStoreConfiguration);
        switch (contentStoreConfiguration) {
        case "hdfs": //$NON-NLS-1$
            String hdfsStoreLocation = environment.getProperty("dataset.content.store.hdfs.location"); //$NON-NLS-1$
            return new HDFSContentStore(hdfsStoreLocation);
        case "local": //$NON-NLS-1$
        default:
            String localStoreLocation = environment.getProperty("dataset.content.store.local.location"); //$NON-NLS-1$
            return new LocalDataSetContentStore(localStoreLocation);
        }
    }

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

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
