package io.ivansanchez16.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
}
