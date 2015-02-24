package org.talend.dataprep.exception;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExceptionsConfiguration {

    @Bean
    public Aspects getAspect() {
        return new Aspects();
    }

}
