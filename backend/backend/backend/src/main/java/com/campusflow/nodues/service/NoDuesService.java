package com.campusflow.nodues.service;

import com.campusflow.department.entity.Department;
import com.campusflow.department.repository.DepartmentRepository;
import com.campusflow.exception.ResourceNotFoundException;
import com.campusflow.nodues.dto.*;
import com.campusflow.nodues.entity.NoDuesDepartmentStatus;
import com.campusflow.nodues.entity.NoDuesRequest;
import com.campusflow.nodues.repository.NoDuesDepartmentStatusRepository;
import com.campusflow.nodues.repository.NoDuesRequestRepository;
import com.campusflow.user.entity.User;
import com.campusflow.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoDuesService {

    private final NoDuesRequestRepository noDuesRequestRepository;
    private final NoDuesDepartmentStatusRepository noDuesDepartmentStatusRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public NoDuesService(NoDuesRequestRepository noDuesRequestRepository,
                         NoDuesDepartmentStatusRepository noDuesDepartmentStatusRepository,
                         UserRepository userRepository,
                         DepartmentRepository departmentRepository) {
        this.noDuesRequestRepository = noDuesRequestRepository;
        this.noDuesDepartmentStatusRepository = noDuesDepartmentStatusRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    public NoDuesResponse initiateNoDues(CreateNoDuesRequest request) {

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        NoDuesRequest noDuesRequest = new NoDuesRequest();
        noDuesRequest.setStudent(student);
        noDuesRequest.setAcademicYear(request.getAcademicYear());
        noDuesRequest.setSemester(request.getSemester());
        noDuesRequest.setOverallStatus("PENDING");
        noDuesRequest.setInitiatedAt(LocalDateTime.now());

        NoDuesRequest saved = noDuesRequestRepository.save(noDuesRequest);
        createDepartmentStatusesForNoDues(saved);

        return mapToResponse(saved);
    }

    public List<NoDuesResponse> getAllNoDuesRequests() {

        List<NoDuesRequest> requests = noDuesRequestRepository.findAll();
        List<NoDuesResponse> responses = new ArrayList<>();

        for (NoDuesRequest request : requests) {
            responses.add(mapToResponse(request));
        }

        return responses;
    }

    public NoDuesResponse getNoDuesById(Long id) {

        NoDuesRequest request = noDuesRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No dues request not found"));

        return mapToResponse(request);
    }

    public NoDuesResponse clearDepartment(Long noDuesId, ClearNoDuesDepartmentRequest request) {

        NoDuesDepartmentStatus status = noDuesDepartmentStatusRepository
                .findByNoDuesRequestIdAndDepartmentId(noDuesId, request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department status not found"));

        User officer = userRepository.findById(request.getOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found"));

        status.setStatus("CLEARED");
        status.setRemarks(request.getRemarks());
        status.setClearedBy(officer);
        status.setClearedAt(LocalDateTime.now());

        noDuesDepartmentStatusRepository.save(status);

        // Check if all cleared
        boolean allCleared = !noDuesDepartmentStatusRepository.existsByNoDuesRequestIdAndStatusNot(noDuesId, "CLEARED");
        if (allCleared) {
            NoDuesRequest noDuesRequest = noDuesRequestRepository.findById(noDuesId).orElseThrow();
            noDuesRequest.setOverallStatus("COMPLETED");
            noDuesRequest.setCompletedAt(LocalDateTime.now());
            noDuesRequestRepository.save(noDuesRequest);
        }

        return getNoDuesById(noDuesId);
    }

    public NoDuesResponse holdDepartment(Long noDuesId, HoldNoDuesDepartmentRequest request) {

        NoDuesDepartmentStatus status = noDuesDepartmentStatusRepository
                .findByNoDuesRequestIdAndDepartmentId(noDuesId, request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department status not found"));

        User officer = userRepository.findById(request.getOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found"));

        status.setStatus("HOLD");
        status.setRemarks(request.getRemarks());
        status.setClearedBy(officer);
        status.setClearedAt(LocalDateTime.now());

        noDuesDepartmentStatusRepository.save(status);

        NoDuesRequest noDuesRequest = noDuesRequestRepository.findById(noDuesId).orElseThrow();
        noDuesRequest.setOverallStatus("HOLD");
        noDuesRequestRepository.save(noDuesRequest);

        return getNoDuesById(noDuesId);
    }

    public NoDuesResponse rejectDepartment(Long noDuesId, RejectNoDuesDepartmentRequest request) {

        NoDuesDepartmentStatus status = noDuesDepartmentStatusRepository
                .findByNoDuesRequestIdAndDepartmentId(noDuesId, request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department status not found"));

        User officer = userRepository.findById(request.getOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found"));

        status.setStatus("REJECTED");
        status.setRemarks(request.getRemarks());
        status.setClearedBy(officer);
        status.setClearedAt(LocalDateTime.now());

        noDuesDepartmentStatusRepository.save(status);

        NoDuesRequest noDuesRequest = noDuesRequestRepository.findById(noDuesId).orElseThrow();
        noDuesRequest.setOverallStatus("REJECTED");
        noDuesRequestRepository.save(noDuesRequest);

        return getNoDuesById(noDuesId);
    }

    private void createDepartmentStatusesForNoDues(NoDuesRequest noDuesRequest) {

        List<Department> departments = departmentRepository.findAll();

        for (Department dept : departments) {

            NoDuesDepartmentStatus status = new NoDuesDepartmentStatus();
            status.setNoDuesRequest(noDuesRequest);
            status.setDepartment(dept);
            status.setStatus("PENDING");

            noDuesDepartmentStatusRepository.save(status);
        }
    }

    private NoDuesResponse mapToResponse(NoDuesRequest request) {

        NoDuesResponse response = new NoDuesResponse();
        response.setId(request.getId());
        response.setStudentName(request.getStudent().getName());
        response.setAcademicYear(request.getAcademicYear());
        response.setSemester(request.getSemester());
        response.setOverallStatus(request.getOverallStatus());
        response.setInitiatedAt(request.getInitiatedAt());
        response.setCompletedAt(request.getCompletedAt());

        List<NoDuesDepartmentStatus> statuses = noDuesDepartmentStatusRepository.findByNoDuesRequestId(request.getId());
        List<NoDuesDepartmentStatusResponse> statusResponses = new ArrayList<>();

        for (NoDuesDepartmentStatus status : statuses) {
            NoDuesDepartmentStatusResponse statusResponse = new NoDuesDepartmentStatusResponse();
            statusResponse.setId(status.getId());
            statusResponse.setDepartment(status.getDepartment().getName());
            statusResponse.setStatus(status.getStatus());
            statusResponse.setRemarks(status.getRemarks());
            statusResponse.setClearedBy(status.getClearedBy() != null ? status.getClearedBy().getName() : null);
            statusResponse.setClearedAt(status.getClearedAt());
            statusResponses.add(statusResponse);
        }

        response.setDepartmentStatuses(statusResponses);

        return response;
    }
}
