package com.example.driveronboardingservice.service;

import com.azure.storage.blob.BlobServiceClient;
import com.example.driveronboardingservice.config.BlobStorageConfig;
import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.exception.GenericException;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ContextConfiguration(classes = {BlobStorageConfig.class, BlobService.class})
@TestPropertySource(locations = "classpath:application-test.properties")
public class BlobServiceTest {

    @Autowired
    private BlobService blobService;
    @Autowired
    private BlobServiceClient blobServiceClient;

    @Test
    public void testStoreDocument() throws IOException, GenericException {
        MultipartFile file = createMockMultipartFile();
        String fileName = "testfile.txt";
        String containerName = "test";

        try {
            blobService.storeDocument(file, fileName, containerName);
        } catch (GenericException e) {
            // Verify that the exception is thrown because the container does not exist
            assertEquals(MessageConstants.GENERIC_ERROR.getCode(), e.getCode());
            assertEquals(MessageConstants.GENERIC_ERROR.getDesc(), e.getMessage());
        }
    }

    @Test
    public void testRetrieveDocument() throws GenericException, ResourceNotFoundException {
        String fileName = "testfile.txt";
        String containerName = "test";

        byte[] expectedData = "Mocked file data".getBytes();

        byte[] retrievedData = blobService.retrieveDocument(fileName, containerName);
        Assertions.assertArrayEquals(expectedData, retrievedData);
    }

    @Test
    public void testDeleteDocument() throws ResourceNotFoundException {
        String fileName = "testfile.txt";
        String containerName = "test";

        blobService.deleteDocument(fileName, containerName);
    }

    // Utility method to create a mock MultipartFile
    private MultipartFile createMockMultipartFile() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "example.pdf",
                "application/pdf",
                "Mocked file data".getBytes()
        );
        return mockMultipartFile;
    }
}