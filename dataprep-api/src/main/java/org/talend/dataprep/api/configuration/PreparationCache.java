package org.talend.dataprep.api.configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.dataprep.preparation.store.ContentCache;

@Configuration
public class PreparationCache {

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
}
