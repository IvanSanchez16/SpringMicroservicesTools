package io.github.ivansanchez16.logger;

import io.github.ivansanchez16.apiresponses.webclient.exceptions.MissingPropertiesException;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.logger")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LogConfig {

    private String transactionHeader;
    private String sessionHeadersPrefix;
    private String[] sessionHeadersList;

    private String projectGroup;

    private final Logger logger = LogManager.getLogger(LogConfig.class.getName());

    @PostConstruct
    void checkVariables() {
        if (transactionHeader == null) {
            throw new MissingPropertiesException("You need to specify a app.logger.transactionHeader property on your configuration file");
        }

        if (sessionHeadersPrefix == null) {
            throw new MissingPropertiesException("You need to specify a app.logger.sessionHeadersPrefix property on your configuration file");
        }

        if (sessionHeadersList == null) {
            throw new MissingPropertiesException("You need to specify a app.logger.sessionHeadersList property on your configuration file");
        }

        if (projectGroup == null) {
            projectGroup = "";
            logger.warn("Property app.logger.projectGroup not defined. It´s recommendable specify a value for better logging");
        }
    }
}
