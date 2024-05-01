package io.ivansanchez16.apiresponses.webclient;

import io.ivansanchez16.apiresponses.webclient.exceptions.MissingPropertiesException;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.core5.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WebConsumerBeansRegistration implements BeanDefinitionRegistryPostProcessor {

    private final Logger logger = LogManager.getLogger(WebConsumerBeansRegistration.class.getName());

    public static final String PROPERTIES_PREFIX = "web-services";
    public static final String PROXY_PROPERTIES_PREFIX = "proxy";

    private final List<WebService> webServices;
    private final WebClient.Builder builder;

    private final String proxyHost;
    private final String proxyPort;

    public WebConsumerBeansRegistration(Environment environment, WebClient.Builder builder) {
        this.webServices = Binder.get(environment)
                .bind(PROPERTIES_PREFIX, Bindable.listOf(WebService.class))
                .orElseThrow( () -> new MissingPropertiesException("The web-services properties is not well formatted") );

        this.proxyHost = Binder.get(environment)
                .bind(PROXY_PROPERTIES_PREFIX+".host", Bindable.of(String.class))
                .orElse("");
        this.proxyPort = Binder.get(environment)
                .bind(PROXY_PROPERTIES_PREFIX+".port", Bindable.of(String.class))
                .orElse("");

        this.builder = builder;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        for (WebService webService : webServices) {
            if (webService.getName() == null || webService.getUrl() == null) {
                continue;
            }

            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();

            beanDefinition.setBeanClass(WebClient.class);

            int connectionTimeout = webService.getConnectionTimeout() != null ? webService.getConnectionTimeout() : 30000;
            int responseTimeout = webService.getResponseTimeout() != null ? webService.getResponseTimeout() : 30000;
            ConnectionConfig connConfig = ConnectionConfig.custom()
                    .setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                    .setSocketTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                    .build();

            PoolingAsyncClientConnectionManager connectionManager = new PoolingAsyncClientConnectionManager();
            connectionManager.setDefaultConnectionConfig(connConfig);

            HttpAsyncClientBuilder clientBuilder = HttpAsyncClients.custom();

            RequestConfig.Builder requestBuilder = RequestConfig.custom();
            requestBuilder.setResponseTimeout(responseTimeout, TimeUnit.MILLISECONDS);

            clientBuilder.setDefaultRequestConfig( requestBuilder.build() );
            clientBuilder.setConnectionManager( connectionManager );

            if (Boolean.TRUE.equals(webService.getUseProxy())) {
                if (proxyHost.isBlank() || proxyPort.isBlank()) {
                    throw new MissingPropertiesException("You need to specify a proxy in your configuration file");
                }

                HttpHost proxy = new HttpHost("http", proxyHost, Integer.parseInt(proxyPort));
                clientBuilder.setProxy(proxy);
            }

            CloseableHttpAsyncClient client = clientBuilder.build();
            ClientHttpConnector connector = new HttpComponentsClientHttpConnector(client);

            beanDefinition.setInstanceSupplier(
                    () ->
                            builder
                                    .baseUrl(webService.getUrl())
                                    .clientConnector(connector)
                                    .build()
            );

            registry.registerBeanDefinition(webService.getName(), beanDefinition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String msg;
        for (WebService webService : webServices) {
            msg = String.format("WebClient bean created with name: \"%s\"",webService.getName());
            logger.info(msg);
        }
    }
}
