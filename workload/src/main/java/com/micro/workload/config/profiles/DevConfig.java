package com.micro.workload.config.profiles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import javax.annotation.PostConstruct;

@Configuration
@Profile("dev")
public class DevConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DevConfig.class);

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("Development Configuration is active");
    }

}