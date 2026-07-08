package com.moneylogix.strategybuilder.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/strategy")
public class PayoffController {

    @Value("${ml.service.url:http://localhost:8000}")
    private String mlServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/payoff")
    public PayoffResponseDto getPayoff(@RequestBody PayoffRequestDto request) {
        String url = mlServiceUrl + "/api/payoff";
        return restTemplate.postForObject(url, request, PayoffResponseDto.class);
    }
}
