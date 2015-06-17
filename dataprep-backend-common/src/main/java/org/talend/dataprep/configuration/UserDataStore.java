package org.talend.dataprep.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.dataprep.store.UserDataRepository;
import org.talend.dataprep.store.local.InMemoryUserDataRepository;
import org.talend.dataprep.store.mongo.MongoUserDateRepository;

@Configuration
@ConditionalOnProperty(name = "user.data.store")
public class UserDataStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataStore.class);

    @Value("${user.data.store}")
    private String userDataStoreConfiguration;

    @Bean
    public UserDataRepository getUserDataRepository() {
        LOGGER.info("User data store: {}", userDataStoreConfiguration); //$NON-NLS-1$
        switch (userDataStoreConfiguration) {
        case "mongodb": //$NON-NLS-1$
            return new MongoUserDateRepository();
        case "in-memory": //$NON-NLS-1$
        default:
            return new InMemoryUserDataRepository();
        }
    }
}
