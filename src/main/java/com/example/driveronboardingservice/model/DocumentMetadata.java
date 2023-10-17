package com.example.driveronboardingservice.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DocumentMetadata {
    private String docName;
    private LocalDate validTill;
    private long stepInstanceId;
}
