package com.example.driveronboardingservice.client;

import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.model.DriverDTO;
import com.example.driveronboardingservice.model.auth.CustomUser;
import com.example.driveronboardingservice.model.response.CreateOrderResponse;
import com.example.driveronboardingservice.service.DriverProfileService;
import com.example.driveronboardingservice.service.auth.CustomUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TrackingDeviceOrderClientTest {

    @Autowired
    private TrackingDeviceOrderClient trackingDeviceOrderClient;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private DriverProfileService driverProfileService;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void testCreateOrder_Success() throws ResourceNotFoundException {
        String driverId = "testDriverId";
        CreateOrderResponse createOrderResponse = new CreateOrderResponse();
        createOrderResponse.setOrderId("testOrderId");

        Mockito.when(driverProfileService.getDriverDetails(driverId))
                .thenReturn(DriverDTO.builder().build());
        Mockito.when(customUserDetailsService.loadUserByUsername(driverId))
                .thenReturn(new CustomUser("user", "password"
                        , List.of("USER"), "user", "9876443210", "user@xyz.com"));
        Mockito.when(restTemplate.exchange(
                anyString(),
                Mockito.eq(HttpMethod.POST),
                any(),
                Mockito.eq(CreateOrderResponse.class))
        ).thenReturn(new ResponseEntity<>(createOrderResponse, HttpStatus.OK));

        CreateOrderResponse response = trackingDeviceOrderClient.createOrder(driverId);
        Assertions.assertEquals("testOrderId", response.getOrderId());
    }

    @Test
    public void testCreateOrder_ServerError() throws ResourceNotFoundException {
        String driverId = "testDriverId";

        Mockito.when(driverProfileService.getDriverDetails(driverId))
                .thenReturn(DriverDTO.builder().build());
        Mockito.when(customUserDetailsService.loadUserByUsername(driverId))
                .thenReturn(new CustomUser("user", "password"
                        , List.of("USER"), "user", "9876443210", "user@xyz.com"));
        Mockito.when(restTemplate.exchange(
                anyString(),
                Mockito.eq(HttpMethod.POST),
                any(),
                Mockito.eq(CreateOrderResponse.class))
        ).thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(HttpServerErrorException.class, () -> trackingDeviceOrderClient.createOrder(driverId));
    }

    @Test
    public void testCreateOrder_ClientError() throws ResourceNotFoundException {
        String driverId = "testDriverId";

        Mockito.when(driverProfileService.getDriverDetails(driverId))
                .thenReturn(DriverDTO.builder().build());
        Mockito.when(customUserDetailsService.loadUserByUsername(driverId))
                .thenReturn(new CustomUser("user", "password"
                        , List.of("USER"), "user", "9876443210", "user@xyz.com"));
        Mockito.when(restTemplate.exchange(
                anyString(),
                Mockito.eq(HttpMethod.POST),
                any(),
                Mockito.eq(CreateOrderResponse.class))
        ).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        assertThrows(HttpClientErrorException.class, () -> trackingDeviceOrderClient.createOrder(driverId));
    }
}
