package com.example.driveronboardingservice.operations;

import com.example.driveronboardingservice.exception.GenericException;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.DocumentDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IDocumentOperations {
    void upload(MultipartFile file, DocumentDTO documentDTO) throws GenericException, ValidationException;

    void delete(Short stepId, String driverId) throws ResourceNotFoundException, ValidationException;

    byte[] download(Short stepId, String driverId) throws ValidationException, ResourceNotFoundException, GenericException;
}

