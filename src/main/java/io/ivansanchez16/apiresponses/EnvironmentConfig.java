package io.ivansanchez16.apiresponses;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AppConfig
 */

@Configuration
@ConfigurationProperties(prefix = "app")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
class EnvironmentConfig {

    private final Logger LOGGER = LogManager.getLogger(EnvironmentConfig.class.getName());

    private String environment;

    @PostConstruct
    void checkEnvironment() {
        if (environment == null) {
            throw new EnvironmentNotSetException();
        }

        Environment env = Environment.getByValue(environment);
        if (env == null) {
            LOGGER.warn("Environment defined in configuration file not found. Ignoring value");
        }
    }
}
