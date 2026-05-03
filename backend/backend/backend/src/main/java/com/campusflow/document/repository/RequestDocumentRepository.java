package com.campusflow.document.repository;

import com.campusflow.document.entity.RequestDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestDocumentRepository extends JpaRepository<RequestDocument, Long> {

    List<RequestDocument> findByRequestId(Long requestId);
}
