package io.ivansanchez16.apiresponses;

import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@UtilityClass
public class MetaResponseConfig {

    @Bean
    public static MetaGenerator createMetaGeneratorBean(EnvironmentConfig environmentConfig) {
        return new MetaGenerator(environmentConfig);
    }
}
