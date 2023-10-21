package com.example.driveronboardingservice.client;

import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.model.DriverDTO;
import com.example.driveronboardingservice.model.auth.CustomUser;
import com.example.driveronboardingservice.model.request.Address;
import com.example.driveronboardingservice.model.request.Contact;
import com.example.driveronboardingservice.model.request.CreateOrderRequest;
import com.example.driveronboardingservice.model.request.ShipTo;
import com.example.driveronboardingservice.model.response.CreateOrderResponse;
import com.example.driveronboardingservice.service.DriverProfileService;
import com.example.driveronboardingservice.service.auth.CustomUserDetailsService;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class TrackingDeviceOrderClient {
    private static final Logger logger = LogManager.getLogger(TrackingDeviceOrderClient.class);

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private DriverProfileService driverProfileService;

    @Autowired
    RestTemplate restTemplate;

    @Value("${tracking.device.order.url}")
    private String orderURL;

    @Retryable(retryFor = HttpServerErrorException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public CreateOrderResponse createOrder(String driverId) throws ResourceNotFoundException {
        DriverDTO driver = driverProfileService.getDriverDetails(driverId);
        CustomUser userDetails = (CustomUser) customUserDetailsService.loadUserByUsername(
                driverId);

        Contact contact = Contact.builder()
                .email(userDetails.getEmail()).phone(userDetails.getPhone()).build();
        Address address = Address.builder()
                .addrLine1(driver.getAddrLine1()).addrLine2(driver.getAddrLine2())
                .city(driver.getCity()).zipCode(driver.getZipCode()).build();
        ShipTo shipTo = ShipTo.builder()
                .name(userDetails.getFullName())
                .address(address).contact(contact).build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .shipTo(shipTo).build();

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
            throw new HttpClientErrorException(createOrderResponse.getStatusCode());
        }
        return createOrderResponse.getBody();
    }

    public HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return headers;
    }
}
