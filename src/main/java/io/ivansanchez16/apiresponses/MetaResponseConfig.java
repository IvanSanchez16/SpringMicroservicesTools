package io.ivansanchez16.apiresponses;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetaResponseConfig {

    @Bean
    public static MetaGenerator createMetaGeneratorBean(EnvironmentConfig environmentConfig) {
        return new MetaGenerator(environmentConfig);
    }

    @Bean
    public static ResponseFilter createResponseFilterBean() {
        return new ResponseFilter();
    }
}
