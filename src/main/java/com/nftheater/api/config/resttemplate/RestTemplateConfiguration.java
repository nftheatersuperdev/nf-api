package com.nftheater.api.config.resttemplate;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.spring.web.TracingClientHttpRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfiguration {

    @Bean
    public HttpTracing create(Tracing tracing) {
        return HttpTracing
                .newBuilder(tracing)
                .build();
    }

    @Bean
    @Primary
    public RestTemplate restTemplate(HttpTracing httpTracing) {
        return new RestTemplateBuilder()
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(getHttpClient(60)))
                .messageConverters(
                        new StringHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter(),
                        new FormHttpMessageConverter())
                .interceptors(List.of(
                        TracingClientHttpRequestInterceptor.create(httpTracing)
                ))
                .errorHandler(new RestTemplateErrorHandler())
                .build();
    }

    private HttpClient getHttpClient(long readTimeout) {
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(5))
                .setResponseTimeout(Timeout.ofSeconds(readTimeout))
                .build();

        final ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(5))
                .setSocketTimeout(Timeout.ofSeconds(60))
                .build();

        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(125);
        connectionManager.setDefaultMaxPerRoute(25);
        connectionManager.setDefaultConnectionConfig(connectionConfig);

        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}
