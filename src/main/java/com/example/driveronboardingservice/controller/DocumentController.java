package com.example.driveronboardingservice.controller;

import com.example.driveronboardingservice.exception.GenericException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.DocumentMetadata;
import com.example.driveronboardingservice.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/document")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public void upload(@RequestParam("metadata")DocumentMetadata metadata, @RequestParam("file") MultipartFile file)
            throws GenericException, ValidationException {
        documentService.upload(file, metadata);
    }
}