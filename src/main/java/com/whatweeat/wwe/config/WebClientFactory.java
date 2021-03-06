package com.whatweeat.wwe.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientFactory {
    private final KakaoConfig kakaoConfig;

    @Bean
    public WebClient oauthWebClient() {
        return WebClient.builder()
                .baseUrl(kakaoConfig.getOauthBase())
                .defaultHeader(HttpHeaders.ACCEPT_CHARSET, "utf-8")
                .build();
    }

    @Bean
    public WebClient apiWebClient() {
        return WebClient.builder()
                .baseUrl(kakaoConfig.getApiBase())
                .defaultHeader(HttpHeaders.ACCEPT_CHARSET, "utf-8")
                .build();
    }
}
