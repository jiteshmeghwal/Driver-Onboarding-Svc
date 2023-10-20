package com.example.driveronboardingservice.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class DocumentDTO {
    private Long docId;
    private String docName;
    private LocalDateTime docUploadTime;
    private LocalDate validTill;
    private Short stepId;
    private String driverId;
}