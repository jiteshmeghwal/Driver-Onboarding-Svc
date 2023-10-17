package com.example.driveronboardingservice.async.listener;

import com.example.driveronboardingservice.dao.entity.Document;
import com.example.driveronboardingservice.dao.entity.OnboardingStepInstance;
import com.example.driveronboardingservice.model.event.DocumentEvent;
import com.example.driveronboardingservice.repository.DocumentRepository;
import com.example.driveronboardingservice.repository.OnboardingStepInstanceRepository;
import com.example.driveronboardingservice.util.EntityMapper;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DocumentEventListener implements ApplicationListener<DocumentEvent> {
    private static final Logger logger = LogManager.getLogger(DocumentEventListener.class);
    private static final EntityMapper entityMapper = new EntityMapper();
    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    OnboardingStepInstanceRepository stepInstanceRepository;

    @Override
    @Async
    @Transactional
    @EventListener
    @Retryable
    public void onApplicationEvent(DocumentEvent event) {
        switch (event.getEventType()) {
            case DOC_UPLOAD -> handleDocUpload(event);
        }
    }

    private void handleDocUpload(DocumentEvent event) {
        logger.info("Handling document upload step by user: {}", event.getUserId());
        Document document = entityMapper.mapDocumentEventToDocumentEntity(event);
        documentRepository.save(document);
        OnboardingStepInstance stepInstance = stepInstanceRepository.findById(
                event.getDocumentMetadata().getStepInstanceId()).get();
        stepInstance.setComplete(true);
        stepInstanceRepository.save(stepInstance);
    }
}
