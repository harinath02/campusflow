package com.campusflow.request.controller;


import com.campusflow.common.ApiResponse;
import com.campusflow.request.dto.ApproveRequest;
import com.campusflow.request.dto.CreateRequestRequest;
import com.campusflow.request.dto.RequestResponse;
import com.campusflow.request.service.RequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.campusflow.request.dto.ApprovalResponse;
import com.campusflow.request.dto.HoldRequest;
import com.campusflow.request.dto.RejectRequest;
import com.campusflow.request.entity.RequestStatus;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RequestResponse>> createRequest(
            @Valid @RequestBody CreateRequestRequest request) {

        RequestResponse response = requestService.createRequest(request);

        return new ResponseEntity<>(
                ApiResponse.<RequestResponse>builder()
                        .status("success")
                        .message("Request created successfully")
                        .data(response)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RequestResponse>> createRequestAlias(
            @Valid @RequestBody CreateRequestRequest request) {
        return createRequest(request);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RequestResponse>>> getAllRequests() {

        List<RequestResponse> response = requestService.getAllRequests();

        return ResponseEntity.ok(
                ApiResponse.<List<RequestResponse>>builder()
                        .status("success")
                        .message("Requests fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse<List<RequestResponse>>> getAllRequestsAlias() {

        return getAllRequests();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RequestResponse>> getRequestById(@PathVariable Long id) {

        RequestResponse response = requestService.getRequestById(id);

        return ResponseEntity.ok(
                ApiResponse.<RequestResponse>builder()
                        .status("success")
                        .message("Request fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/approve")
    public ResponseEntity<ApiResponse<String>> approveRequest(
            @Valid @RequestBody ApproveRequest request) {

        requestService.approveRequest(request);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status("success")
                        .message("Request approved")
                        .data(null)
                        .build()
        );
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<RequestResponse>> submitRequest(@PathVariable Long id) {

        RequestResponse response = requestService.submitRequest(id);

        return ResponseEntity.ok(
                ApiResponse.<RequestResponse>builder()
                        .status("success")
                        .message("Request submitted successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/reject")
    public ResponseEntity<ApiResponse<String>> rejectRequest(
            @Valid @RequestBody RejectRequest request) {

        requestService.rejectRequest(request);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status("success")
                        .message("Request rejected")
                        .data(null)
                        .build()
        );
    }

    @GetMapping("/{id}/approvals")
    public ResponseEntity<ApiResponse<List<ApprovalResponse>>> getApprovalsByRequestId(@PathVariable Long id) {

        List<ApprovalResponse> response = requestService.getApprovalsByRequestId(id);

        return ResponseEntity.ok(
                ApiResponse.<List<ApprovalResponse>>builder()
                        .status("success")
                        .message("Approvals fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ApiResponse<List<RequestResponse>>> getRequestsByUser(@PathVariable Long userId) {

        List<RequestResponse> response = requestService.getRequestsByUser(userId);

        return ResponseEntity.ok(
                ApiResponse.<List<RequestResponse>>builder()
                        .status("success")
                        .message("Requests fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<ApiResponse<List<RequestResponse>>> getRequestsByStatus(@PathVariable String status) {

        RequestStatus requestStatus = RequestStatus.valueOf(status.toUpperCase());
        List<RequestResponse> response = requestService.getRequestsByStatus(requestStatus);

        return ResponseEntity.ok(
                ApiResponse.<List<RequestResponse>>builder()
                        .status("success")
                        .message("Requests fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/hold")
    public ResponseEntity<ApiResponse<String>> holdRequest(
            @Valid @RequestBody HoldRequest request) {

        requestService.holdRequest(request);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status("success")
                        .message("Request placed on hold")
                        .data(null)
                        .build()
        );
    }

    @GetMapping("/by-department/{departmentId}")
    public ResponseEntity<ApiResponse<List<RequestResponse>>> getRequestsByDepartment(@PathVariable Long departmentId) {

        List<RequestResponse> response = requestService.getRequestsByDepartment(departmentId);

        return ResponseEntity.ok(
                ApiResponse.<List<RequestResponse>>builder()
                        .status("success")
                        .message("Department requests fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/by-department/{departmentId}/pending")
    public ResponseEntity<ApiResponse<List<RequestResponse>>> getPendingRequestsByDepartment(@PathVariable Long departmentId) {

        List<RequestResponse> response = requestService.getPendingRequestsByDepartment(departmentId);

        return ResponseEntity.ok(
                ApiResponse.<List<RequestResponse>>builder()
                        .status("success")
                        .message("Pending department requests fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/by-type/{requestTypeId}")
    public ResponseEntity<ApiResponse<List<RequestResponse>>> getRequestsByType(@PathVariable Long requestTypeId) {

        List<RequestResponse> response = requestService.getRequestsByType(requestTypeId);

        return ResponseEntity.ok(
                ApiResponse.<List<RequestResponse>>builder()
                        .status("success")
                        .message("Requests fetched successfully")
                        .data(response)
                        .build()
        );
    }
}
