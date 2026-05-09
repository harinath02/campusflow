package com.campusflow.document.service;

import com.campusflow.document.dto.DocumentResponse;
import com.campusflow.document.entity.RequestDocument;
import com.campusflow.document.repository.RequestDocumentRepository;
import com.campusflow.exception.ResourceNotFoundException;
import com.campusflow.request.repository.RequestRepository;
import com.campusflow.user.entity.User;
import com.campusflow.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {

    private final RequestDocumentRepository requestDocumentRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    private final String uploadDir = "uploads/";

    public DocumentService(RequestDocumentRepository requestDocumentRepository,
                           RequestRepository requestRepository,
                           UserRepository userRepository) {
        this.requestDocumentRepository = requestDocumentRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    public DocumentResponse uploadDocument(Long requestId, Long uploadedById, MultipartFile file) throws IOException {

        // Verify request exists
        requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        User uploadedBy = userRepository.findById(uploadedById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create directory if not exists
        Path uploadPath = Paths.get(uploadDir + requestId);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        // Save metadata
        RequestDocument document = new RequestDocument();
        document.setRequest(requestRepository.findById(requestId).get());
        document.setFileName(fileName);
        document.setFilePath(filePath.toString());
        document.setContentType(file.getContentType());
        document.setUploadedBy(uploadedBy);
        document.setUploadedAt(LocalDateTime.now());

        RequestDocument saved = requestDocumentRepository.save(document);

        return mapToResponse(saved);
    }

    public List<DocumentResponse> getDocumentsByRequestId(Long requestId) {

        List<RequestDocument> documents = requestDocumentRepository.findByRequestId(requestId);
        List<DocumentResponse> responses = new ArrayList<>();

        for (RequestDocument doc : documents) {
            responses.add(mapToResponse(doc));
        }

        return responses;
    }

    private DocumentResponse mapToResponse(RequestDocument document) {

        DocumentResponse response = new DocumentResponse();
        response.setId(document.getId());
        response.setRequestId(document.getRequest().getId());
        response.setFileName(document.getFileName());
        response.setContentType(document.getContentType());
        response.setUploadedBy(document.getUploadedBy().getName());
        response.setUploadedAt(document.getUploadedAt());

        return response;
    }
}
