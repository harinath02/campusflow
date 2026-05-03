package com.campusflow.requesttype.controller;

import com.campusflow.common.ApiResponse;
import com.campusflow.requesttype.dto.CreateRequestTypeRequest;
import com.campusflow.requesttype.dto.RequestTypeResponse;
import com.campusflow.requesttype.service.RequestTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request-types")
public class RequestTypeController {

    private final RequestTypeService requestTypeService;

    public RequestTypeController(RequestTypeService requestTypeService) {
        this.requestTypeService = requestTypeService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RequestTypeResponse>> createRequestType(
            @Valid @RequestBody CreateRequestTypeRequest request) {

        RequestTypeResponse response = requestTypeService.createRequestType(request);

        return new ResponseEntity<>(
                ApiResponse.<RequestTypeResponse>builder()
                        .status("success")
                        .message("Request type created successfully")
                        .data(response)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/get")
    public ResponseEntity<ApiResponse<List<RequestTypeResponse>>> getAllRequestTypes() {

        List<RequestTypeResponse> response = requestTypeService.getAllRequestTypes();

        return ResponseEntity.ok(
                ApiResponse.<List<RequestTypeResponse>>builder()
                        .status("success")
                        .message("Request types fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RequestTypeResponse>> getRequestTypeById(@PathVariable Long id) {

        RequestTypeResponse response = requestTypeService.getRequestTypeById(id);

        return ResponseEntity.ok(
                ApiResponse.<RequestTypeResponse>builder()
                        .status("success")
                        .message("Request type fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RequestTypeResponse>> updateRequestType(@PathVariable Long id, @Valid @RequestBody CreateRequestTypeRequest request) {

        RequestTypeResponse response = requestTypeService.updateRequestType(id, request);

        return ResponseEntity.ok(
                ApiResponse.<RequestTypeResponse>builder()
                        .status("success")
                        .message("Request type updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deactivateRequestType(@PathVariable Long id) {

        requestTypeService.deactivateRequestType(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status("success")
                        .message("Request type deactivated successfully")
                        .data(null)
                        .build()
        );
    }
}