package com.campusflow.nodues.controller;

import com.campusflow.common.ApiResponse;
import com.campusflow.nodues.dto.*;
import com.campusflow.nodues.service.NoDuesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/no-dues")
public class NoDuesController {

    private final NoDuesService noDuesService;

    public NoDuesController(NoDuesService noDuesService) {
        this.noDuesService = noDuesService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NoDuesResponse>> initiateNoDues(
            @Valid @RequestBody CreateNoDuesRequest request) {

        NoDuesResponse response = noDuesService.initiateNoDues(request);

        return new ResponseEntity<>(
                ApiResponse.<NoDuesResponse>builder()
                        .status("success")
                        .message("No dues request initiated successfully")
                        .data(response)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NoDuesResponse>>> getAllNoDuesRequests() {

        List<NoDuesResponse> response = noDuesService.getAllNoDuesRequests();

        return ResponseEntity.ok(
                ApiResponse.<List<NoDuesResponse>>builder()
                        .status("success")
                        .message("No dues requests fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoDuesResponse>> getNoDuesById(@PathVariable Long id) {

        NoDuesResponse response = noDuesService.getNoDuesById(id);

        return ResponseEntity.ok(
                ApiResponse.<NoDuesResponse>builder()
                        .status("success")
                        .message("No dues request fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/clear")
    public ResponseEntity<ApiResponse<NoDuesResponse>> clearDepartment(
            @PathVariable Long id,
            @Valid @RequestBody ClearNoDuesDepartmentRequest request) {

        NoDuesResponse response = noDuesService.clearDepartment(id, request);

        return ResponseEntity.ok(
                ApiResponse.<NoDuesResponse>builder()
                        .status("success")
                        .message("Department cleared successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/hold")
    public ResponseEntity<ApiResponse<NoDuesResponse>> holdDepartment(
            @PathVariable Long id,
            @Valid @RequestBody HoldNoDuesDepartmentRequest request) {

        NoDuesResponse response = noDuesService.holdDepartment(id, request);

        return ResponseEntity.ok(
                ApiResponse.<NoDuesResponse>builder()
                        .status("success")
                        .message("Department held successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<NoDuesResponse>> rejectDepartment(
            @PathVariable Long id,
            @Valid @RequestBody RejectNoDuesDepartmentRequest request) {

        NoDuesResponse response = noDuesService.rejectDepartment(id, request);

        return ResponseEntity.ok(
                ApiResponse.<NoDuesResponse>builder()
                        .status("success")
                        .message("Department rejected successfully")
                        .data(response)
                        .build()
        );
    }
}
