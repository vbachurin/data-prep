package org.talend.dataprep.dataset.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.datascience.statistics.StatisticsClientJson;

@Configuration
public class DataScienceStatistics {

    @Bean
    StatisticsClientJson getStatisticsClientJson() {
        StatisticsClientJson statisticsClient = new StatisticsClientJson(true);
        statisticsClient.setJsonRecordPath("records"); //$NON-NLS-1$
        return statisticsClient;
    }
}
