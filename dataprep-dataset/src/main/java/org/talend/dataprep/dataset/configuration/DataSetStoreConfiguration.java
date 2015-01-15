package org.talend.dataprep.dataset.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.talend.dataprep.dataset.store.DataSetContentStore;
import org.talend.dataprep.dataset.store.DataSetMetadataRepository;
import org.talend.dataprep.dataset.store.local.InMemoryDataSetMetadataRepository;
import org.talend.dataprep.dataset.store.local.LocalDataSetContentStore;
import org.talend.dataprep.dataset.store.mongo.MongoDataSetMetadataRepository;

@Configuration
public class DataSetStoreConfiguration implements EnvironmentAware {

    private static final Log LOGGER = LogFactory.getLog(DataSetStoreConfiguration.class);

    @Value("${dataset.metadata.store}")
    private String           metadataStoreConfiguration;

    @Value("${dataset.content.store}")
    private String           contentStoreConfiguration;

    private Environment environment;

    @Bean
    public DataSetContentStore getContentStore() {
        LOGGER.info("Data Set content store: " + contentStoreConfiguration);
        String localStoreLocation = environment.getProperty("dataset.content.store.local.location"); //$NON-NLS-1$
        if ("local".equalsIgnoreCase(contentStoreConfiguration)) { //$NON-NLS-1$
            return new LocalDataSetContentStore(localStoreLocation);
        } else {
            return new LocalDataSetContentStore(localStoreLocation);
        }
    }

    @Bean
    public DataSetMetadataRepository getStore() {
        LOGGER.info("Data Set metadata store: " + metadataStoreConfiguration);
        if ("mongodb".equalsIgnoreCase(metadataStoreConfiguration)) { //$NON-NLS-1$
            return new MongoDataSetMetadataRepository();
        } else if ("in-memory".equalsIgnoreCase(metadataStoreConfiguration)) { //$NON-NLS-1$
            return new InMemoryDataSetMetadataRepository();
        } else {
            return new InMemoryDataSetMetadataRepository();
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
