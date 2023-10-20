package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.EventType;
import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.dao.CustomJDBCQueryExecutor;
import com.example.driveronboardingservice.dao.entity.Document;
import com.example.driveronboardingservice.exception.GenericException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.model.event.StepCompleteEvent;
import com.example.driveronboardingservice.repository.DocumentRepository;
import com.example.driveronboardingservice.util.RequestContextStore;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.util.Optional;

@Service
@Transactional
public class DocumentService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private BlobService blobService;
    @Autowired
    private CustomJDBCQueryExecutor jdbcQueryExecutor;
    @Autowired
    private OnboardingStepService onboardingStepService;
    @Autowired
    private DocumentRepository documentRepository;

    public void upload(MultipartFile file, Short stepId) throws GenericException,
            ValidationException {
        String driverId = RequestContextStore.getUser().getUsername();
        validateOnboardingStep(stepId, driverId);
        validateDocumentNotExist(driverId, stepId);
        OnboardingStepDTO onboardingStepDTO = onboardingStepService.updateCompleteStatus(stepId, driverId
                , true);
        
        blobService.storeDocument(file, driverId);
        
        eventPublisher.publishEvent(getStepCompleteEvent(onboardingStepDTO, driverId));
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