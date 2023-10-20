package com.example.driveronboardingservice.async;

import com.example.driveronboardingservice.client.TrackingDeviceOrderClient;
import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.model.DriverDTO;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.model.ShipmentDTO;
import com.example.driveronboardingservice.model.auth.CustomUser;
import com.example.driveronboardingservice.model.event.StepCompleteEvent;
import com.example.driveronboardingservice.model.request.Address;
import com.example.driveronboardingservice.model.request.Contact;
import com.example.driveronboardingservice.model.request.CreateOrderRequest;
import com.example.driveronboardingservice.model.request.ShipTo;
import com.example.driveronboardingservice.model.response.CreateOrderResponse;
import com.example.driveronboardingservice.service.DriverProfileService;
import com.example.driveronboardingservice.service.OnboardingStepService;
import com.example.driveronboardingservice.service.ShipmentService;
import com.example.driveronboardingservice.service.auth.CustomUserDetailsService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StepCompleteEventListener implements ApplicationListener<StepCompleteEvent> {
    private static final Logger logger = LogManager.getLogger(StepCompleteEventListener.class);

    @Autowired
    private OnboardingStepService onboardingStepService;

    @Autowired
    private TrackingDeviceOrderClient trackingDeviceOrderClient;

    @Autowired
    private DriverProfileService driverProfileService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ShipmentService shipmentService;


    @Override
    @Async
    @Transactional
    @EventListener
    @Retryable
    public void onApplicationEvent(StepCompleteEvent event) {
        logger.info("Received stepId {} completion event for user {}",
                event.getOnboardingStep().getStepId(), event.getUserId());
        Optional<OnboardingStepDTO> nextIncompleteStep = onboardingStepService.getNextIncompleteStep(
                event.getUserId()
        );
        if(nextIncompleteStep.isPresent()) {
            if(OnboardingStepType.SHIPMENT.getCode().equals(nextIncompleteStep.get().getStepTypeCd())) {
                try {
                    DriverDTO driver = driverProfileService.getDriverDetails(event.getUserId());
                    CustomUser userDetails = (CustomUser) customUserDetailsService.loadUserByUsername(
                            event.getUserId());
                    Contact contact = Contact.builder()
                            .email(userDetails.getEmail()).phone(userDetails.getPhone()).build();
                    Address address = Address.builder()
                            .addrLine1(driver.getAddrLine1()).addrLine2(driver.getAddrLine2())
                            .city(driver.getCity()).zipCode(driver.getZipCode()).build();
                    ShipTo shipTo = ShipTo.builder()
                            .name(userDetails.getFullName())
                            .address(address).contact(contact).build();
                    CreateOrderResponse response = trackingDeviceOrderClient.createOrder(
                            CreateOrderRequest.builder()
                                    .shipTo(shipTo).build()
                    );
                    shipmentService.createShipment(
                            ShipmentDTO.builder()
                                    .orderId(response.getOrderId())
                                    .driverId(event.getUserId())
                                    .stepId(event.getOnboardingStep().getStepId()).build()
                    );
                } catch (ResourceNotFoundException e) {
                    logger.error("Driver profile not found for user {}", event.getUserId());
                }
            }
        }
    }
}
