package com.example.driveronboardingservice.model.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateOrderRequest {
    private ShipTo shipTo;
}
