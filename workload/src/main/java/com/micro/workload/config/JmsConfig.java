package com.micro.workload.config;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;

@Configuration
@EnableJms
public class JmsConfig {

    @Value("${my.jms.broker-url}")
    private String brokerUrl;

    @Value("${my.jms.user}")
    private String user;

    @Value("${my.jms.password}")
    private String password;

    @Value("${my.jms.queue-name}")
    private String queueName;

    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerUrl);
        cf.setUserName(user);
        cf.setPassword(password);
        return cf;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory(ConnectionFactory cf) {
        return new CachingConnectionFactory(cf);
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(CachingConnectionFactory cachingConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
        factory.setConcurrency("1-1");
        factory.setSessionTransacted(false);
        return factory;
    }
}


