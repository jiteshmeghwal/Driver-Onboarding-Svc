package com.example.driveronboardingservice.async.kafka.consumer;

import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.ShipmentDTO;
import com.example.driveronboardingservice.service.OnboardingStepService;
import com.example.driveronboardingservice.service.ShipmentService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

@ExtendWith(MockitoExtension.class)
class ShipmentEventConsumerTest {
    @Mock
    private ShipmentService shipmentService;

    @InjectMocks
    ShipmentEventConsumer shipmentEventConsumer;

    private final String message = "{ \"orderId\": 123, \"carrier\": \"TestCarrier\", \"statusCd\": 3 }";
    private final ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>("test-shipment-update-topic",
            0, 0, "key", message);

    @Test
    void consume() {
        Acknowledgment acknowledge = Mockito.mock(Acknowledgment.class);
        shipmentEventConsumer.consume(consumerRecord, acknowledge);
        Mockito.verify(acknowledge, Mockito.times(1)).acknowledge();
    }

    @Test
    void consume_validation_exception() throws ValidationException {
        Acknowledgment acknowledge = Mockito.mock(Acknowledgment.class);
        Mockito.doThrow(new ValidationException(MessageConstants.SHIPMENT_NOT_FOUND.getCode(),
                MessageConstants.SHIPMENT_NOT_FOUND.getDesc()))
                .when(shipmentService).updateShipment(Mockito.any(ShipmentDTO.class));
        shipmentEventConsumer.consume(consumerRecord, acknowledge);
        Mockito.verify(acknowledge, Mockito.times(1)).acknowledge();
    }
}