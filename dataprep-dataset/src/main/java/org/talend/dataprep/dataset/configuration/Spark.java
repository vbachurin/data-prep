package org.talend.dataprep.dataset.configuration;

import java.util.UUID;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Spark {

    @Bean
    public SparkContext getSparkContext() {
        SparkConf conf = new SparkConf();
        conf.set("spark.ui.enabled", "false");
        return new SparkContext("local[4]", UUID.randomUUID().toString(), conf);
    }
}
