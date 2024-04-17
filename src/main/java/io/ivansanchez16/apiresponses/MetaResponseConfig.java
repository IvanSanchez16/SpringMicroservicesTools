package io.ivansanchez16.apiresponses;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetaResponseConfig {

    public MetaResponseConfig() {
        // Se deja en blanco para crear beans necesarios
    }

    @Bean
    public static MetaGenerator createMetaGeneratorBean(EnvironmentConfig environmentConfig) {
        return new MetaGenerator(environmentConfig);
    }
}
