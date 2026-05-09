package com.campusflow.document.controller;

import com.campusflow.common.ApiResponse;
import com.campusflow.document.dto.DocumentResponse;
import com.campusflow.document.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/{requestId}/documents")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @PathVariable Long requestId,
            @RequestParam Long uploadedById,
            @RequestParam("file") MultipartFile file) throws IOException {

        DocumentResponse response = documentService.uploadDocument(requestId, uploadedById, file);

        return new ResponseEntity<>(
                ApiResponse.<DocumentResponse>builder()
                        .status("success")
                        .message("Document uploaded successfully")
                        .data(response)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{requestId}/documents")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getDocumentsByRequestId(@PathVariable Long requestId) {

        List<DocumentResponse> response = documentService.getDocumentsByRequestId(requestId);

        return ResponseEntity.ok(
                ApiResponse.<List<DocumentResponse>>builder()
                        .status("success")
                        .message("Documents fetched successfully")
                        .data(response)
                        .build()
        );
    }
}
