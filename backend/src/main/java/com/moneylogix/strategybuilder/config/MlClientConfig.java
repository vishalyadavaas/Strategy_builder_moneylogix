package com.moneylogix.strategybuilder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class MlClientConfig {

    @Bean
    public RestClient mlRestClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:8000")
                .requestFactory(new SimpleClientHttpRequestFactory())
                .build();
    }
}