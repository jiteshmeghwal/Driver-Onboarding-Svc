package com.example.driveronboardingservice.repository;

import org.springframework.data.repository.CrudRepository;
import com.example.driveronboardingservice.dao.entity.Document;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {

}
