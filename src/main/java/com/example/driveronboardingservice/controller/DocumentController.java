package com.example.driveronboardingservice.controller;

import com.example.driveronboardingservice.exception.GenericException;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.DocumentDTO;
import com.example.driveronboardingservice.service.DocumentService;
import com.example.driveronboardingservice.util.RequestContextStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/document")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @PostMapping
    public void upload(@RequestHeader("stepId")Short stepId,
                       @RequestParam(value = "validTill", required = false)
                       @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate validTill,
                       @RequestParam("file") MultipartFile file)
            throws GenericException, ValidationException {
        documentService.upload(file, DocumentDTO.builder()
                        .stepId(stepId)
                        .validTill(validTill)
                        .driverId(RequestContextStore.getUser().getUsername())
                        .build());
    }

    @GetMapping
    public byte[] download(@RequestHeader("stepId") Short stepId) throws ValidationException,
            ResourceNotFoundException, GenericException {
        return documentService.download(stepId, RequestContextStore.getUser().getUsername());
    }

    @DeleteMapping
    public void delete(@RequestHeader("stepId") Short stepId) throws ValidationException,
            ResourceNotFoundException {
        documentService.delete(stepId, RequestContextStore.getUser().getUsername());
    }
}