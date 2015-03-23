package org.talend.dataprep.i18n;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class I18N {

    @Bean
    public ResourceBundleMessageSource getResourceBundle() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages"); //$NON-NLS-1$
        source.setUseCodeAsDefaultMessage(false);
        return source;
    }
}
