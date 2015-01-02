package org.talend.dataprep.dataset.configuration;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class Messaging {

    @Bean
    public JmsTemplate getJmsTemplate() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        PooledConnectionFactory pool = new PooledConnectionFactory();
        pool.setConnectionFactory(connectionFactory);
        JmsTemplate jmsTemplate = new JmsTemplate(pool);
        jmsTemplate.setDeliveryPersistent(false);
        return jmsTemplate;
    }

}
