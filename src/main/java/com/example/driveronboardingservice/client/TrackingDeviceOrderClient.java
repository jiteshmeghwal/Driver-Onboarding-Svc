package com.example.driveronboardingservice.client;

import com.example.driveronboardingservice.model.request.CreateOrderRequest;
import com.example.driveronboardingservice.model.response.CreateOrderResponse;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class TrackingDeviceOrderClient {
    private static final Logger logger = LogManager.getLogger(TrackingDeviceOrderClient.class);

    @Autowired
    RestTemplate restTemplate;

    @Value("${tracking.device.order.url}")
    private String orderURL;

    @Retryable(retryFor = HttpServerErrorException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        logger.info("Create order request: {}", new Gson().toJson(request));
        HttpEntity<CreateOrderRequest> requestHttpEntity = new HttpEntity<>(request, getHttpHeaders());
        ResponseEntity<CreateOrderResponse> createOrderResponse =
                     restTemplate.exchange(
                             orderURL,
                             HttpMethod.POST,
                             requestHttpEntity,
                             CreateOrderResponse.class
                     );
        if (createOrderResponse.getStatusCode().is5xxServerError()) {
            // Handle server error (5xx)
            logger.error("Rest call to create order for tracking device failed with payload: {}",
                    createOrderResponse.getBody());
            throw new HttpServerErrorException(createOrderResponse.getStatusCode());
        }
        if(createOrderResponse.getStatusCode().is4xxClientError()) {
            logger.error("Bad request to create order for tracking device: {}",
                    createOrderResponse.getBody());
        }
        return createOrderResponse.getBody();
    }

    public HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return headers;
    }
}
