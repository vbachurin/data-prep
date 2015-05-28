package org.talend.dataprep.transformation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.talend.dataprep")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
