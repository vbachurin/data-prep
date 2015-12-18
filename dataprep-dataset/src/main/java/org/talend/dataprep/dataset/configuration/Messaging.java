package org.talend.dataprep.dataset.configuration;

import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@SuppressWarnings("InsufficientBranchCoverage")
public class Messaging {

    @Bean
    public JmsTemplate getJmsTemplate() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost"); //$NON-NLS-1$
        connectionFactory.setOptimizeAcknowledge(true);
        connectionFactory.setAlwaysSyncSend(true);
        PooledConnectionFactory pool = new PooledConnectionFactory();
        pool.setConnectionFactory(connectionFactory);
        JmsTemplate jmsTemplate = new JmsTemplate(pool);
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return jmsTemplate;
    }

}
