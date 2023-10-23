package com.example.driveronboardingservice.service;

import com.example.driveronboardingservice.constant.OnboardingStepType;
import com.example.driveronboardingservice.entity.Document;
import com.example.driveronboardingservice.exception.GenericException;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.DocumentDTO;
import com.example.driveronboardingservice.model.OnboardingStepDTO;
import com.example.driveronboardingservice.repository.DocumentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {
    @Mock
    private OnboardingStepService onboardingStepService;
    @Mock
    private BlobService blobService;
    @Mock
    private DocumentRepository documentRepository;
    @InjectMocks
    private DocumentService documentService;
    private Short stepId = (short)1;
    private String driverId = "user";

    @Test
    void upload_TestInvalidStep_NotDocUpload() throws ValidationException {
        Mockito.when(onboardingStepService.getOnboardingStep(stepId, driverId)).thenReturn(
                OnboardingStepDTO.builder().stepTypeCd(OnboardingStepType.BACKGROUND_VERIFICATION.getCode())
                        .build()
        );

        Assertions.assertThrows(ValidationException.class,
                ()->documentService.upload(createMockMultipartFile(), getDocumentDTO()));
    }

    @Test
    void upload_TestInvalidStep_StepComplete() throws ValidationException {
        Mockito.when(onboardingStepService.getOnboardingStep(stepId, driverId)).thenReturn(
                OnboardingStepDTO.builder().stepTypeCd(OnboardingStepType.DOC_UPLOAD.getCode())
                        .isComplete(true)
                        .build()
        );

        Assertions.assertThrows(ValidationException.class,
                ()->documentService.upload(createMockMultipartFile(), getDocumentDTO()));
    }

    @Test
    void upload_TestDocumentAlreadyExist() throws ValidationException {
        Mockito.when(onboardingStepService.getOnboardingStep(stepId, driverId)).thenReturn(
                OnboardingStepDTO.builder().stepTypeCd(OnboardingStepType.DOC_UPLOAD.getCode())
                        .isComplete(false)
                        .build()
        );

        Mockito.when(documentRepository.findByDriverIdAndStepId(
                driverId, stepId
        )).thenReturn(Optional.of(new Document()));


        Assertions.assertThrows(ValidationException.class,
                ()->documentService.upload(createMockMultipartFile(), getDocumentDTO()));
    }

    @Test
    void upload() throws ValidationException, GenericException {
        Mockito.when(onboardingStepService.getOnboardingStep(stepId, driverId)).thenReturn(
                OnboardingStepDTO.builder().stepTypeCd(OnboardingStepType.DOC_UPLOAD.getCode())
                        .isComplete(false)
                        .build()
        );

        documentService.upload(createMockMultipartFile(), getDocumentDTO());
    }

    @Test
    void delete_DocumentNotFound() {
        Assertions.assertThrows(ValidationException.class,()->
                documentService.delete(stepId, driverId));
    }

    @Test
    void delete() throws ValidationException, ResourceNotFoundException {
        Mockito.when(documentRepository.findByDriverIdAndStepId(driverId, stepId)).thenReturn(
                Optional.of(new Document())
        );
        documentService.delete(stepId, driverId);
    }

    @Test
    void download_DocumentNotFound() {
        Assertions.assertThrows(ValidationException.class,()->
                documentService.download(stepId, driverId));
    }

    @Test
    void download() throws ValidationException, ResourceNotFoundException, GenericException {
        Mockito.when(documentRepository.findByDriverIdAndStepId(driverId, stepId)).thenReturn(
                Optional.of(new Document())
        );
        documentService.download(stepId, driverId);
    }

    private MultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "file",
                "example.pdf",
                "application/pdf",
                "Mocked file data".getBytes()
        );
    }
    
    private DocumentDTO getDocumentDTO() {
        return DocumentDTO.builder().driverId(driverId).stepId(stepId).build();
    }
}