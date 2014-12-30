package org.talend.dataprep.dataset.store;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSetStoreConfiguration {

    @Bean
    public DataSetRepository getStore() {
        return new InMemoryDataSetRepository();
    }
}
