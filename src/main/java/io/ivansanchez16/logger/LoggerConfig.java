package io.ivansanchez16.logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerConfig {

    public LoggerConfig() {
        // Se deja en blanco para crear beans necesarios
    }

    @Bean
    public static RequestInfoFilter createRequestInfoFilterBean(LogConfig logConfig) {
        return new RequestInfoFilter(logConfig.getTransactionHeader(), logConfig.getSessionHeadersPrefix(),
                logConfig.getSessionHeadersList());
    }
    
    @Bean
    public static RequestInfoHelper createRequestInfoHelper() {
        return new RequestInfoHelper();
    }

    @Bean
    public static LogMethods createLogMethods(LogConfig logConfig, RequestInfoHelper requestInfoHelper) {
        return new LogMethods(logConfig, requestInfoHelper);
    }
}
