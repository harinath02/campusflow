package com.campusflow.request.service;

import com.campusflow.audit.service.AuditService;
import com.campusflow.department.entity.Department;
import com.campusflow.department.repository.DepartmentRepository;
import com.campusflow.exception.BadRequestException;
import com.campusflow.exception.ResourceNotFoundException;
import com.campusflow.notification.service.NotificationService;
import com.campusflow.request.dto.*;
import com.campusflow.request.entity.Request;
import com.campusflow.request.entity.RequestApproval;
import com.campusflow.request.entity.RequestStatus;
import com.campusflow.request.repository.RequestApprovalRepository;
import com.campusflow.request.repository.RequestRepository;
import com.campusflow.requesttype.entity.RequestType;
import com.campusflow.requesttype.repository.RequestTypeRepository;
import com.campusflow.role.entity.RoleName;
import com.campusflow.user.entity.User;
import com.campusflow.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RequestService {

    private static final int DEFAULT_SLA_DAYS = 3;

    private final RequestRepository requestRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RequestApprovalRepository requestApprovalRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    public RequestService(RequestRepository requestRepository,
                          RequestTypeRepository requestTypeRepository,
                          UserRepository userRepository,
                          DepartmentRepository departmentRepository,
                          RequestApprovalRepository requestApprovalRepository,
                          NotificationService notificationService,
                          AuditService auditService) {
        this.requestRepository = requestRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.requestApprovalRepository = requestApprovalRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    public RequestResponse createRequest(CreateRequestRequest dto) {
        RequestType requestType = requestTypeRepository.findById(dto.getRequestTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Request type not found"));

        if (Boolean.FALSE.equals(requestType.getActive())) {
            throw new BadRequestException("Request type is inactive");
        }

        User requester = userRepository.findById(dto.getRequesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Requester not found"));

        LocalDateTime now = LocalDateTime.now();
        Request request = new Request();
        request.setRequestNumber(generateRequestNumber());
        request.setRequestType(requestType);
        request.setRequester(requester);
        request.setTitle(dto.getTitle());
        request.setDescription(dto.getDescription());
        request.setPriority(normalizePriority(dto.getPriority()));
        request.setStatus(RequestStatus.SUBMITTED);
        request.setCreatedAt(now);
        request.setSubmittedAt(now);
        request.setExpectedCompletionTime(now.plusDays(DEFAULT_SLA_DAYS));
        request.setDelayed(false);

        Request saved = requestRepository.save(request);
        createApprovalsForRequest(saved);

        auditService.log(requester.getId(), requester.getName(), "REQUEST_CREATED", "REQUEST", saved.getId(),
                "Request created: " + saved.getRequestNumber());
        auditService.log(requester.getId(), requester.getName(), "REQUEST_SUBMITTED", "REQUEST", saved.getId(),
                "Request submitted to departments: " + saved.getRequestNumber());
        notifyRoutingAudience(saved, "New request submitted",
                saved.getRequestNumber() + " from " + requester.getName() + " is ready for review.");

        return mapToResponse(saved);
    }

    public List<RequestResponse> getAllRequests() {
        List<RequestResponse> responses = new ArrayList<>();
        for (Request request : requestRepository.findAll()) {
            responses.add(mapToResponse(request));
        }
        return responses;
    }

    public RequestResponse getRequestById(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        return mapToResponse(request);
    }

    public void approveRequest(ApproveRequest dto) {
        RequestApproval approval = getApproval(dto.getRequestId(), dto.getDepartmentId());
        Request request = approval.getRequest();
        validateOpenForDecision(request);

        approval.setStatus(RequestStatus.APPROVED);
        approval.setRemarks(defaultRemark(dto.getRemarks(), "Approved by department."));
        approval.setActionAt(LocalDateTime.now());
        requestApprovalRepository.save(approval);

        boolean allApproved = requestApprovalRepository.findByRequestId(dto.getRequestId())
                .stream()
                .allMatch(item -> item.getStatus() == RequestStatus.APPROVED);

        if (allApproved) {
            request.setStatus(RequestStatus.COMPLETED);
            request.setActualCompletionTime(LocalDateTime.now());
            request.setDelayed(isDelayed(request));
            notificationService.notifyUser(request.getRequester().getId(), "Request completed",
                    request.getRequestNumber() + " has been approved by all departments.");
        } else {
            request.setStatus(RequestStatus.IN_REVIEW);
            notificationService.notifyUser(request.getRequester().getId(), "Department approved",
                    approval.getDepartment().getName() + " approved " + request.getRequestNumber() + ".");
        }

        requestRepository.save(request);
        User actor = findActor(dto.getActorUserId());
        auditService.log(actor == null ? null : actor.getId(),
                actor == null ? approval.getDepartment().getName() : actor.getName(),
                "REQUEST_APPROVED", "REQUEST", request.getId(),
                approval.getDepartment().getName() + " approved " + request.getRequestNumber());
    }

    public RequestResponse submitRequest(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (request.getStatus() == RequestStatus.COMPLETED || request.getStatus() == RequestStatus.REJECTED
                || request.getStatus() == RequestStatus.CANCELLED) {
            throw new BadRequestException("Closed requests cannot be submitted again");
        }

        if (request.getStatus() == RequestStatus.DRAFT) {
            request.setStatus(RequestStatus.SUBMITTED);
            request.setSubmittedAt(LocalDateTime.now());
            if (request.getExpectedCompletionTime() == null) {
                request.setExpectedCompletionTime(LocalDateTime.now().plusDays(DEFAULT_SLA_DAYS));
            }
            if (requestApprovalRepository.findByRequestId(request.getId()).isEmpty()) {
                createApprovalsForRequest(request);
            }
            Request saved = requestRepository.save(request);
            auditService.log(request.getRequester().getId(), request.getRequester().getName(), "REQUEST_SUBMITTED",
                    "REQUEST", saved.getId(), "Request submitted: " + saved.getRequestNumber());
            notifyRoutingAudience(saved, "Request submitted",
                    saved.getRequestNumber() + " is ready for department review.");
            return mapToResponse(saved);
        }

        return mapToResponse(request);
    }

    public void rejectRequest(RejectRequest dto) {
        RequestApproval approval = getApproval(dto.getRequestId(), dto.getDepartmentId());
        Request request = approval.getRequest();
        validateOpenForDecision(request);

        approval.setStatus(RequestStatus.REJECTED);
        approval.setRemarks(defaultRemark(dto.getRemarks(), "Rejected by department."));
        approval.setActionAt(LocalDateTime.now());
        requestApprovalRepository.save(approval);

        request.setStatus(RequestStatus.REJECTED);
        request.setActualCompletionTime(LocalDateTime.now());
        request.setDelayed(isDelayed(request));
        requestRepository.save(request);

        notificationService.notifyUser(request.getRequester().getId(), "Request rejected",
                approval.getDepartment().getName() + " rejected " + request.getRequestNumber() + ".");

        User actor = findActor(dto.getActorUserId());
        auditService.log(actor == null ? null : actor.getId(),
                actor == null ? approval.getDepartment().getName() : actor.getName(),
                "REQUEST_REJECTED", "REQUEST", request.getId(),
                approval.getDepartment().getName() + " rejected " + request.getRequestNumber());
    }

    public void holdRequest(HoldRequest dto) {
        RequestApproval approval = getApproval(dto.getRequestId(), dto.getDepartmentId());
        Request request = approval.getRequest();
        validateOpenForDecision(request);

        approval.setStatus(RequestStatus.ON_HOLD);
        approval.setRemarks(defaultRemark(dto.getRemarks(), "Kept on hold pending clarification."));
        approval.setActionAt(LocalDateTime.now());
        requestApprovalRepository.save(approval);

        request.setStatus(RequestStatus.ON_HOLD);
        request.setDelayed(isDelayed(request));
        requestRepository.save(request);

        notificationService.notifyUser(request.getRequester().getId(), "Request on hold",
                approval.getDepartment().getName() + " kept " + request.getRequestNumber() + " on hold.");
    }

    public List<ApprovalResponse> getApprovalsByRequestId(Long requestId) {
        requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        List<ApprovalResponse> responses = new ArrayList<>();
        for (RequestApproval approval : requestApprovalRepository.findByRequestId(requestId)) {
            responses.add(mapApprovalToResponse(approval));
        }
        return responses;
    }

    public List<RequestResponse> getRequestsByUser(Long userId) {
        List<RequestResponse> responses = new ArrayList<>();
        for (Request request : requestRepository.findByRequesterId(userId)) {
            responses.add(mapToResponse(request));
        }
        return responses;
    }

    public List<RequestResponse> getRequestsByStatus(RequestStatus status) {
        List<RequestResponse> responses = new ArrayList<>();
        for (Request request : requestRepository.findByStatus(status)) {
            responses.add(mapToResponse(request));
        }
        return responses;
    }

    public List<RequestResponse> getRequestsByDepartment(Long departmentId) {
        departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        List<RequestResponse> responses = new ArrayList<>();
        for (RequestApproval approval : requestApprovalRepository.findByDepartmentId(departmentId)) {
            responses.add(mapToResponse(approval.getRequest()));
        }
        return responses;
    }

    public List<RequestResponse> getPendingRequestsByDepartment(Long departmentId) {
        departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        List<RequestResponse> responses = new ArrayList<>();
        for (RequestApproval approval : requestApprovalRepository.findByDepartmentIdAndStatus(departmentId, RequestStatus.IN_REVIEW)) {
            Request request = approval.getRequest();
            if (request.getStatus() != RequestStatus.REJECTED && request.getStatus() != RequestStatus.COMPLETED) {
                responses.add(mapToResponse(request));
            }
        }
        return responses;
    }

    public List<RequestResponse> getRequestsByType(Long requestTypeId) {
        List<RequestResponse> responses = new ArrayList<>();
        for (Request request : requestRepository.findByRequestTypeId(requestTypeId)) {
            responses.add(mapToResponse(request));
        }
        return responses;
    }

    private RequestResponse mapToResponse(Request request) {
        RequestResponse response = new RequestResponse();
        response.setId(request.getId());
        response.setRequestNumber(request.getRequestNumber());
        response.setRequestTypeId(request.getRequestType().getId());
        response.setRequestType(request.getRequestType().getName());
        response.setRequesterId(request.getRequester().getId());
        response.setRequesterName(request.getRequester().getName());
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setStatus(request.getStatus().name());
        response.setPriority(request.getPriority());
        response.setCreatedAt(request.getCreatedAt());
        response.setSubmittedAt(request.getSubmittedAt());
        response.setExpectedCompletionTime(request.getExpectedCompletionTime());
        response.setActualCompletionTime(request.getActualCompletionTime());
        response.setDelayed(isDelayed(request));
        return response;
    }

    private ApprovalResponse mapApprovalToResponse(RequestApproval approval) {
        ApprovalResponse response = new ApprovalResponse();
        response.setId(approval.getId());
        response.setRequestId(approval.getRequest().getId());
        response.setDepartmentId(approval.getDepartment().getId());
        response.setDepartment(approval.getDepartment().getName());
        response.setStatus(approval.getStatus().name());
        response.setRemarks(approval.getRemarks());
        response.setActionAt(approval.getActionAt());
        return response;
    }

    private RequestApproval getApproval(Long requestId, Long departmentId) {
        return requestApprovalRepository.findByRequestIdAndDepartmentId(requestId, departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Approval not found"));
    }

    private void createApprovalsForRequest(Request request) {
        for (Department dept : departmentRepository.findAll()) {
            RequestApproval approval = new RequestApproval();
            approval.setRequest(request);
            approval.setDepartment(dept);
            approval.setStatus(RequestStatus.IN_REVIEW);
            requestApprovalRepository.save(approval);
        }
    }

    private void validateOpenForDecision(Request request) {
        if (request.getStatus() == RequestStatus.REJECTED || request.getStatus() == RequestStatus.COMPLETED
                || request.getStatus() == RequestStatus.CANCELLED) {
            throw new BadRequestException("Closed requests cannot be changed");
        }
    }

    private boolean isDelayed(Request request) {
        if (request.getExpectedCompletionTime() == null) {
            return false;
        }
        LocalDateTime completionReference = request.getActualCompletionTime() == null
                ? LocalDateTime.now()
                : request.getActualCompletionTime();
        return completionReference.isAfter(request.getExpectedCompletionTime());
    }

    private void notifyRoutingAudience(Request request, String title, String message) {
        for (RequestApproval approval : requestApprovalRepository.findByRequestId(request.getId())) {
            notificationService.notifyDepartment(approval.getDepartment().getId(), title, message);
        }
        notificationService.notifyRole(RoleName.ADMIN, title, message);
    }

    private User findActor(Long actorUserId) {
        if (actorUserId == null) {
            return null;
        }
        return userRepository.findById(actorUserId).orElse(null);
    }

    private String normalizePriority(String priority) {
        return priority == null || priority.isBlank() ? "MEDIUM" : priority.toUpperCase();
    }

    private String defaultRemark(String remarks, String fallback) {
        return remarks == null || remarks.isBlank() ? fallback : remarks;
    }

    private String generateRequestNumber() {
        return "REQ-" + System.currentTimeMillis();
    }
}
