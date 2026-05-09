package com.campusflow.complaint.controller;

import com.campusflow.complaint.dto.ComplaintResponse;
import com.campusflow.complaint.dto.CreateComplaintRequest;
import com.campusflow.complaint.service.ComplaintService;
import com.campusflow.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ComplaintResponse>> createComplaint(
            @Valid @RequestBody CreateComplaintRequest request) {

        ComplaintResponse response = complaintService.createComplaint(request);

        return new ResponseEntity<>(
                ApiResponse.<ComplaintResponse>builder()
                        .status("success")
                        .message("Complaint created successfully")
                        .data(response)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getAllComplaints() {

        List<ComplaintResponse> response = complaintService.getAllComplaints();

        return ResponseEntity.ok(
                ApiResponse.<List<ComplaintResponse>>builder()
                        .status("success")
                        .message("Complaints fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<String>> resolveComplaint(
            @PathVariable Long id,
            @RequestParam String resolutionNote) {

        complaintService.resolveComplaint(id, resolutionNote);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status("success")
                        .message("Complaint resolved successfully")
                        .data(null)
                        .build()
        );
    }
}
