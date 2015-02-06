package org.talend.dataprep.api.configuration;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("org.talend.dataprep.api.configuration")
@EnableSwagger
public class Documentation {

    private SpringSwaggerConfig springSwaggerConfig;

    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }

    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
        ApiInfo apiInfo = new ApiInfo("Talend Data Preparation - API",
                "This service exposes high level services that may involve services orchestration.", StringUtils.EMPTY, StringUtils.EMPTY,
                StringUtils.EMPTY, StringUtils.EMPTY);
        return new SwaggerSpringMvcPlugin(springSwaggerConfig).apiInfo(apiInfo).includePatterns(".*api.*"); //$NON-NLS-1
    }
}
