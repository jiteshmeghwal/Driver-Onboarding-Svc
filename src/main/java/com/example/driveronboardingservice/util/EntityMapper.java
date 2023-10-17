package com.example.driveronboardingservice.util;

import com.example.driveronboardingservice.dao.entity.Document;
import com.example.driveronboardingservice.dao.entity.DriverProfile;
import com.example.driveronboardingservice.dao.entity.Vehicle;
import com.example.driveronboardingservice.model.auth.CustomUser;
import com.example.driveronboardingservice.model.event.DocumentEvent;
import com.example.driveronboardingservice.model.request.GenericDriverProfileRequest;

import java.sql.Timestamp;
import java.util.List;

public class EntityMapper {

    private DriverProfile initDriverProfile() {
        CustomUser user = RequestContextStore.getUser();
        DriverProfile profile = new DriverProfile();
        profile.setDriverId(user.getUsername());
        return profile;
    }

    private Vehicle mapVehicleEntity(GenericDriverProfileRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType(request.getVehicleTypeCode());
        vehicle.setModel(request.getVehicleModel());
        vehicle.setRegNo(request.getVehicleRegNo());
        return vehicle;
    }

    public DriverProfile mapCreateDriverRequestToDriverProfile(GenericDriverProfileRequest createRequest) {
        DriverProfile profile = initDriverProfile();
        profile.setCity(createRequest.getCity());
        profile.setZipCode(createRequest.getZipCode());
        profile.setAvailable(false);
        profile.setVehicles(List.of(mapVehicleEntity(createRequest)));
        return profile;
    }

    public Document mapDocumentEventToDocumentEntity(DocumentEvent documentEvent) {
        Document document = new Document();
        document.setDocName(documentEvent.getDocumentMetadata().getDocName());
        document.setValidTill(Timestamp.valueOf(documentEvent.getDocumentMetadata().getValidTill().atStartOfDay()));
        document.setDocUploadTime(new Timestamp(documentEvent.getTimestamp()));
        document.setDriverId(documentEvent.getUserId());
        document.setStepInstanceId(documentEvent.getDocumentMetadata().getStepInstanceId());
        return document;
    }
}
