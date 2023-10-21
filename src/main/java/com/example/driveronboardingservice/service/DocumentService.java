package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.EventType;
import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.entity.Document;
import com.example.driveronboardingservice.exception.GenericException;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.DocumentDTO;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.model.event.StepCompleteEvent;
import com.example.driveronboardingservice.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DocumentService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private BlobService blobService;
    @Autowired
    private OnboardingStepService onboardingStepService;
    @Autowired
    private DocumentRepository documentRepository;

    public void upload(MultipartFile file, DocumentDTO documentDTO) throws GenericException,
            ValidationException {
        validateOnboardingStep(documentDTO.getStepId(), documentDTO.getDriverId());
        validateDocumentNotExist(documentDTO.getDriverId(), documentDTO.getStepId());

        OnboardingStepDTO onboardingStepDTO = onboardingStepService.updateCompleteStatus(
                documentDTO.getStepId(), documentDTO.getDriverId()
                , true);

        documentDTO.setDocName(getUniqueFileName(file.getOriginalFilename()));
        documentRepository.save(createDocument(documentDTO));
        
        blobService.storeDocument(file, documentDTO.getDocName(), documentDTO.getDriverId());

        onboardingStepService.publishEvent(getStepCompleteEvent(onboardingStepDTO, documentDTO.getDriverId()));
    }

    public void delete(Short stepId, String driverId) throws ResourceNotFoundException, ValidationException {
        validateOnboardingStep(stepId, driverId);
        Optional<Document> document = documentRepository.findByDriverIdAndStepId(driverId, stepId);
        if(document.isPresent()) {
            String fileName = document.get().getDocName();
            documentRepository.delete(document.get());
            blobService.deleteDocument(fileName, driverId);
        } else {
            throw new ValidationException(MessageConstants.DOCUMENT_NOT_FOUND.getCode(),
                    MessageConstants.DOCUMENT_NOT_FOUND.getDesc());
        }
    }

    public byte[] download(Short stepId, String driverId) throws ValidationException, ResourceNotFoundException, GenericException {
        Optional<Document> document = documentRepository.findByDriverIdAndStepId(driverId, stepId);
        if(document.isPresent()) {
            return blobService.retrieveDocument(document.get().getDocName(), driverId);
        } else {
            throw new ValidationException(MessageConstants.DOCUMENT_NOT_FOUND.getCode(),
                    MessageConstants.DOCUMENT_NOT_FOUND.getDesc());
        }
    }

    private Document createDocument(DocumentDTO documentDTO) {
        Document document = new Document();
        document.setDocName(documentDTO.getDocName());
        document.setDocUploadTime(Timestamp.from(Instant.now()));
        document.setValidTill(Timestamp.valueOf(documentDTO.getValidTill().atStartOfDay()));
        document.setDriverId(documentDTO.getDriverId());
        document.setStepId(documentDTO.getStepId());
        return document;
    }

    private String getUniqueFileName(String originalFileName) throws ValidationException {
        if(Objects.isNull(originalFileName)) {
            throw new ValidationException(MessageConstants.FILE_NAME_IS_NULL.getCode(),
                    MessageConstants.FILE_NAME_IS_NULL.getDesc());
        }
        String uniqueIdentifier = UUID.randomUUID().toString();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return uniqueIdentifier + fileExtension;
    }

    private void validateDocumentNotExist(String driverId, Short stepId) throws ValidationException {
        Optional<Document> document = documentRepository.findByDriverIdAndStepId(
                driverId, stepId
        );
        if(document.isPresent()) {
            throw new ValidationException(MessageConstants.DOCUMENT_ALREADY_EXIST.getCode(),
                    MessageConstants.DOCUMENT_ALREADY_EXIST.getDesc());
        }
    }

    private void validateOnboardingStep(Short stepId, String driverId) throws ValidationException {
        OnboardingStepDTO step = onboardingStepService.getOnboardingStep(stepId, driverId);

        if (!OnboardingStepType.DOC_UPLOAD.getCode().equals(step.getStepTypeCd())) {
            throw new ValidationException(MessageConstants.INVALID_STEP.getCode(),
                    MessageConstants.INVALID_STEP.getDesc());
        }
        if(step.isComplete()) {
            throw new ValidationException(MessageConstants.STEP_IS_ALREADY_COMPLETE.getCode(),
                    MessageConstants.STEP_IS_ALREADY_COMPLETE.getDesc());
        }
    }

    private StepCompleteEvent getStepCompleteEvent(OnboardingStepDTO onboardingStep, String driverId) {
        StepCompleteEvent stepCompleteEvent = new StepCompleteEvent(this, Clock.systemUTC());
        stepCompleteEvent.setEventType(EventType.STEP_COMPLETE);
        stepCompleteEvent.setUserId(driverId);
        stepCompleteEvent.setOnboardingStep(onboardingStep);
        return  stepCompleteEvent;
    }

}