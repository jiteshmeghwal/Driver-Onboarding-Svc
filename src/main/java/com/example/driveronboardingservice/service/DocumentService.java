package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.EventType;
import com.example.driveronboardingservice.exception.GenericException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.DocumentMetadata;
import com.example.driveronboardingservice.model.auth.CustomUser;
import com.example.driveronboardingservice.model.event.DocumentEvent;
import com.example.driveronboardingservice.util.RequestContextStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;

@Service
public class DocumentService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private BlobService blobService;
    @Autowired
    private ValidatorService validatorService;

    public void upload(MultipartFile file, DocumentMetadata metadata) throws GenericException,
            ValidationException {
        CustomUser user = RequestContextStore.getUser();
        validatorService.validateDocumentUploadRequest(metadata);
        blobService.storeDocument(file, metadata.getDocName(), user.getUsername());
        eventPublisher.publishEvent(getDocumentUploadEvent(user, metadata));
    }

    private DocumentEvent getDocumentUploadEvent(CustomUser user
            , DocumentMetadata metadata) {
        DocumentEvent documentEvent = new DocumentEvent(this, Clock.systemUTC());
        documentEvent.setEventType(EventType.DOC_UPLOAD);
        documentEvent.setUserId(user.getUsername());
        documentEvent.setDocumentMetadata(metadata);
        return documentEvent;
    }
}