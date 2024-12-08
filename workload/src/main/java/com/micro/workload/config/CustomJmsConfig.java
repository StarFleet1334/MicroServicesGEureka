package com.micro.workload.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.workload.model.dto.TrainingSessionDTO;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

@Configuration
public class CustomJmsConfig {

    private final ObjectMapper objectMapper;

    public CustomJmsConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public JmsTemplate jmsTemplate(jakarta.jms.ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(customMessageConverter());
        return jmsTemplate;
    }

    @Bean
    public MessageConverter customMessageConverter() {
        return new MessageConverter() {
            @Override
            public jakarta.jms.Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
                if (object instanceof TrainingSessionDTO) {
                    try {
                        String json = objectMapper.writeValueAsString(object);
                        return session.createTextMessage(json);
                    } catch (JsonProcessingException e) {
                        throw new MessageConversionException("Could not convert TrainingSessionDTO to JSON", e);
                    }
                }
                throw new MessageConversionException("Unsupported message type: " + object.getClass().getName());
            }

            @Override
            public Object fromMessage(jakarta.jms.Message message) throws JMSException, MessageConversionException {
                if (message instanceof TextMessage) {
                    try {
                        String json = ((TextMessage) message).getText();
                        return objectMapper.readValue(json, TrainingSessionDTO.class);
                    } catch (JsonProcessingException e) {
                        throw new MessageConversionException("Could not convert JSON to TrainingSessionDTO", e);
                    }
                }
                throw new MessageConversionException("Unsupported message type");
            }
        };
    }
}