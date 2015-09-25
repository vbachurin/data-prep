package org.talend.dataprep.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebApplication extends WebMvcConfigurerAdapter {

    @Autowired
    private ResourceProperties resourceProperties = new ResourceProperties();

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/ui/**")) {
            registry.addResourceHandler("/ui")
                    .addResourceLocations("classpath:/META-INF/resources/dist/index.html");
            registry.addResourceHandler("/ui/**")
                    .addResourceLocations("classpath:/META-INF/resources/dist/");
            registry.addResourceHandler("/assets/**")
                    .addResourceLocations("classpath:/META-INF/resources/dist/assets/");
        }

    }

}
