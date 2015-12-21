package org.talend.dataprep.i18n;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
@SuppressWarnings("InsufficientBranchCoverage")
public class I18N {

    @Bean
    public ResourceBundleMessageSource getResourceBundle() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        String[] baseNames = new String[] { "org.talend.dataprep.messages", "org.talend.dataprep.error_messages" };
        source.setBasenames(baseNames); // $NON-NLS-1$
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }
}
