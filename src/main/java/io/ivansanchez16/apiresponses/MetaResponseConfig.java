package io.ivansanchez16.apiresponses;

import io.ivansanchez16.logger.LogConfig;
import io.ivansanchez16.logger.LogMethods;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetaResponseConfig {

    public MetaResponseConfig() {
        // Se deja en blanco para crear beans necesarios
    }

    @Bean
    public static MetaGenerator createMetaGeneratorBean(EnvironmentConfig environmentConfig,
                                                        LogConfig logConfig, LogMethods logMethods) {
        return new MetaGenerator(environmentConfig, logMethods, logConfig.getTransactionHeader());
    }
}
