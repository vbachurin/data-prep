package org.talend.dataprep.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class CORSConfiguration {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Add CORS header for all path in application
                registry.addMapping("/**") //
                        .allowedOrigins("*") //
                        .allowedMethods("POST", "GET", "OPTIONS", "DELETE", "PUT") //
                        .maxAge(3600) //
                        .allowedHeaders("x-requested-with", "Content-Type", "accept")
                        .allowCredentials(true);
            }
        };
    }

}
