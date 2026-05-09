package com.campusflow.complaint.service;

import com.campusflow.complaint.dto.ComplaintResponse;
import com.campusflow.complaint.dto.CreateComplaintRequest;
import com.campusflow.complaint.entity.ComplaintRequest;
import com.campusflow.complaint.repository.ComplaintRequestRepository;
import com.campusflow.department.entity.Department;
import com.campusflow.department.repository.DepartmentRepository;
import com.campusflow.exception.ResourceNotFoundException;
import com.campusflow.request.entity.Request;
import com.campusflow.request.entity.RequestStatus;
import com.campusflow.request.repository.RequestRepository;
import com.campusflow.requesttype.entity.RequestType;
import com.campusflow.requesttype.repository.RequestTypeRepository;
import com.campusflow.user.entity.User;
import com.campusflow.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRequestRepository complaintRequestRepository;
    private final RequestRepository requestRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public ComplaintService(ComplaintRequestRepository complaintRequestRepository,
                            RequestRepository requestRepository,
                            RequestTypeRepository requestTypeRepository,
                            UserRepository userRepository,
                            DepartmentRepository departmentRepository) {
        this.complaintRequestRepository = complaintRequestRepository;
        this.requestRepository = requestRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    public ComplaintResponse createComplaint(CreateComplaintRequest request) {

        RequestType requestType = requestTypeRepository.findAll().stream()
                .filter(rt -> rt.getName().equals("Complaint"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Complaint request type not found"));

        User requester = userRepository.findById(request.getRequesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Requester not found"));

        Department assignedDepartment = departmentRepository.findById(request.getAssignedDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assigned department not found"));

        Request req = new Request();
        req.setRequestNumber("COMP-" + System.currentTimeMillis());
        req.setRequestType(requestType);
        req.setRequester(requester);
        req.setTitle(request.getTitle());
        req.setDescription(request.getDescription());
        req.setPriority("HIGH"); // Assuming complaints are high priority
        req.setStatus(RequestStatus.DRAFT);
        req.setCreatedAt(LocalDateTime.now());

        Request savedRequest = requestRepository.save(req);

        ComplaintRequest complaintRequest = new ComplaintRequest();
        complaintRequest.setRequest(savedRequest);
        complaintRequest.setCategory(request.getCategory());
        complaintRequest.setLocation(request.getLocation());
        complaintRequest.setSeverity(request.getSeverity());
        complaintRequest.setAssignedDepartment(assignedDepartment);

        complaintRequestRepository.save(complaintRequest);

        return mapToResponse(complaintRequest);
    }

    public List<ComplaintResponse> getAllComplaints() {

        List<ComplaintRequest> complaints = complaintRequestRepository.findAll();
        List<ComplaintResponse> responses = new ArrayList<>();

        for (ComplaintRequest comp : complaints) {
            responses.add(mapToResponse(comp));
        }

        return responses;
    }

    public void resolveComplaint(Long complaintId, String resolutionNote) {

        ComplaintRequest complaint = complaintRequestRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        complaint.setResolutionNote(resolutionNote);
        complaint.setResolvedAt(LocalDateTime.now());

        complaintRequestRepository.save(complaint);

        // Update request status to COMPLETED
        Request req = complaint.getRequest();
        req.setStatus(RequestStatus.COMPLETED);
        requestRepository.save(req);
    }

    private ComplaintResponse mapToResponse(ComplaintRequest complaintRequest) {

        ComplaintResponse response = new ComplaintResponse();
        response.setId(complaintRequest.getId());
        response.setRequestId(complaintRequest.getRequest().getId());
        response.setRequestNumber(complaintRequest.getRequest().getRequestNumber());
        response.setCategory(complaintRequest.getCategory());
        response.setLocation(complaintRequest.getLocation());
        response.setSeverity(complaintRequest.getSeverity());
        response.setAssignedDepartment(complaintRequest.getAssignedDepartment().getName());
        response.setStatus(complaintRequest.getRequest().getStatus().name());
        response.setResolutionNote(complaintRequest.getResolutionNote());
        response.setResolvedAt(complaintRequest.getResolvedAt());

        return response;
    }
}
