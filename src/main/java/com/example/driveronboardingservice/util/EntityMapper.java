package com.example.driveronboardingservice.util;

import com.example.driveronboardingservice.entity.DriverProfile;
import com.example.driveronboardingservice.entity.Vehicle;
import com.example.driveronboardingservice.model.auth.CustomUser;
import com.example.driveronboardingservice.model.request.GenericDriverProfileRequest;

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
        profile.setAddrLine1(createRequest.getAddrLine1());
        profile.setAddrLine2(createRequest.getAddrLine2());
        profile.setCity(createRequest.getCity());
        profile.setZipCode(createRequest.getZipCode());
        profile.setAvailable(false);
        profile.setVehicles(List.of(mapVehicleEntity(createRequest)));
        return profile;
    }
}
