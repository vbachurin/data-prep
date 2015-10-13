package org.talend.dataprep.application;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.talend.dataprep.application.configuration.APIConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class WebApplication extends WebMvcConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebApplication.class);

    @Autowired
    private ResourceProperties resourceProperties = new ResourceProperties();

    @Value("${server.port}")
    private int port;

    @Controller
    public static class FaviconController {
        @RequestMapping("/favicon.ico")
        String favicon() {
            return "forward:/assets/images/favicon.png";
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!this.resourceProperties.isAddMappings()) {
            return;
        }
        // Assets
        if (!registry.hasMappingForPattern("/assets/**")) {
            registry.addResourceHandler("/assets/**")
                    .addResourceLocations("classpath:/META-INF/resources/dist/assets/")
                    .resourceChain(false)
                    .addTransformer(new APIConfigResourceTransformer());
        }
        // UI
        if (!registry.hasMappingForPattern("/ui/**")) {
            registry.addResourceHandler("/ui/**")
                    .addResourceLocations("classpath:/META-INF/resources/dist/");
        }
    }

    private class APIConfigResourceTransformer implements ResourceTransformer {

        @Override
        public Resource transform(HttpServletRequest request, Resource resource,
                                  ResourceTransformerChain transformerChain) throws IOException {
            resource = transformerChain.transform(request, resource);
            if ("config.json".equals(resource.getFilename())) {
                try {
                    // Build custom configuration
                    final APIConfig config = new APIConfig();
                    config.setServerUrl("http://127.0.0.1:" + port);
                    final StringWriter writer = new StringWriter();
                    final ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.writer().writeValue(writer, config);
                    // Return a resource for this JSON configuration
                    LOGGER.debug("Web UI configuration content: {}.", writer);
                    return new AbstractResource() {

                        @Override
                        public String getFilename() {
                            return "config.json";
                        }

                        @Override
                        public String getDescription() {
                            return "API Configuration";
                        }

                        @Override
                        public long lastModified() throws IOException {
                            return 0;
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            return new ByteArrayInputStream(writer.toString().getBytes());
                        }
                    };
                } catch (IOException e) {
                    LOGGER.error("Unable to provide Web UI configuration.", e);
                    return resource;
                }
            }
            return resource;
        }
    }
}
