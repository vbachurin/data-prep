package org.talend.dataprep.configuration;

import java.util.UUID;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty("dataset.spark.master")
public class Spark {

    @Value("${dataset.spark.master}")
    private String sparkMasterUrl;

    @Bean
    public SparkContext getSparkContext() {
        SparkConf conf = new SparkConf();
        conf.set("spark.ui.enabled", "false"); //$NON-NLS-1$ //$NON-NLS-2$
        return new SparkContext(sparkMasterUrl, UUID.randomUUID().toString(), conf);
    }
}
