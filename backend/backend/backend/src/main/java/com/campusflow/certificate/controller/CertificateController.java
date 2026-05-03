package com.campusflow.certificate.controller;

import com.campusflow.certificate.dto.CertificateResponse;
import com.campusflow.certificate.dto.CreateCertificateRequest;
import com.campusflow.certificate.service.CertificateService;
import com.campusflow.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CertificateResponse>> createCertificateRequest(
            @Valid @RequestBody CreateCertificateRequest request) {

        CertificateResponse response = certificateService.createCertificateRequest(request);

        return new ResponseEntity<>(
                ApiResponse.<CertificateResponse>builder()
                        .status("success")
                        .message("Certificate request created successfully")
                        .data(response)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CertificateResponse>>> getAllCertificates() {

        List<CertificateResponse> response = certificateService.getAllCertificates();

        return ResponseEntity.ok(
                ApiResponse.<List<CertificateResponse>>builder()
                        .status("success")
                        .message("Certificates fetched successfully")
                        .data(response)
                        .build()
        );
    }
}
