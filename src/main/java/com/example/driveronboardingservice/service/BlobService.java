package com.example.driveronboardingservice.service;

import com.azure.storage.blob.*;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.example.driveronboardingservice.constant.MessageConstants;
import com.example.driveronboardingservice.exception.GenericException;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class BlobService {
    private static final Logger logger = LogManager.getLogger(BlobService.class);

    @Autowired
    private BlobServiceClient blobServiceClient;

    public void storeDocument(MultipartFile file, String blobName, String containerName) throws GenericException {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        if(!containerClient.exists()) containerClient.create();

        BlockBlobClient client = containerClient.getBlobClient(blobName)
                .getBlockBlobClient();

        // Upload the file to Azure Blob Storage
        try (ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes())) {
                client.upload(stream, file.getSize(), true);
        } catch (IOException exception) {
            logger.error("failed reading multipart file with error message : {}",exception.getMessage());
            throw new GenericException(MessageConstants.GENERIC_ERROR.getCode(),
                    MessageConstants.GENERIC_ERROR.getDesc());
        }
    }

    public byte[] retrieveDocument(String fileName, String containerName) throws IOException, ResourceNotFoundException {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(fileName);

        if (blobClient.exists()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            blobClient.download(outputStream);
            return outputStream.toByteArray();
        } else {
            throw new ResourceNotFoundException(MessageConstants.DOCUMENT_NOT_FOUND.getCode(),
                    MessageConstants.DOCUMENT_NOT_FOUND.getDesc());
        }
    }

    public void deleteDocument(String fileName, String containerName) throws ResourceNotFoundException {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(fileName);

        if (blobClient.exists()) {
            blobClient.delete();
        } else {
            throw new ResourceNotFoundException(MessageConstants.DOCUMENT_NOT_FOUND.getCode(),
                    MessageConstants.DOCUMENT_NOT_FOUND.getDesc());
        }
    }
}
