package com.micro.workload.config.profiles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

@Configuration
@Profile("test")
public class TestConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalConfig.class);

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("Test Configuration is active");
    }

}
