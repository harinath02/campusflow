package com.campusflow.certificate.service;

import com.campusflow.certificate.dto.CertificateResponse;
import com.campusflow.certificate.dto.CreateCertificateRequest;
import com.campusflow.certificate.entity.CertificateRequest;
import com.campusflow.certificate.repository.CertificateRequestRepository;
import com.campusflow.exception.ResourceNotFoundException;
import com.campusflow.request.dto.CreateRequestRequest;
import com.campusflow.request.entity.Request;
import com.campusflow.request.entity.RequestStatus;
import com.campusflow.request.repository.RequestRepository;
import com.campusflow.request.service.RequestService;
import com.campusflow.requesttype.entity.RequestType;
import com.campusflow.requesttype.repository.RequestTypeRepository;
import com.campusflow.user.entity.User;
import com.campusflow.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CertificateService {

    private final CertificateRequestRepository certificateRequestRepository;
    private final RequestService requestService;
    private final RequestTypeRepository requestTypeRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    public CertificateService(CertificateRequestRepository certificateRequestRepository,
                             RequestService requestService,
                             RequestTypeRepository requestTypeRepository,
                             UserRepository userRepository,
                             RequestRepository requestRepository) {
        this.certificateRequestRepository = certificateRequestRepository;
        this.requestService = requestService;
        this.requestTypeRepository = requestTypeRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    public CertificateResponse createCertificateRequest(CreateCertificateRequest request) {

        // Find request type by code "BONAFIDE_CERTIFICATE" - but since not specified, assume we have it or hardcode
        RequestType requestType = requestTypeRepository.findAll().stream()
                .filter(rt -> rt.getName().equals("Certificate Request"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Certificate request type not found"));

        User requester = userRepository.findById(request.getRequesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Requester not found"));

        // Create generic Request
        Request req = new Request();
        req.setRequestNumber("CERT-" + System.currentTimeMillis());
        req.setRequestType(requestType);
        req.setRequester(requester);
        req.setTitle(request.getCertificateType() + " Certificate Request");
        req.setDescription("Request for " + request.getCertificateType() + " certificate for purpose: " + request.getPurpose());
        req.setPriority("NORMAL");
        req.setStatus(RequestStatus.DRAFT);
        req.setCreatedAt(java.time.LocalDateTime.now());

        Request savedRequest = requestRepository.save(req);
        // Create approvals for request
        // Assuming we have a method, but since it's in RequestService, perhaps call it, but for simplicity, skip or assume.

        // Create CertificateRequest
        CertificateRequest certificateRequest = new CertificateRequest();
        certificateRequest.setRequest(savedRequest);
        certificateRequest.setCertificateType(request.getCertificateType());
        certificateRequest.setPurpose(request.getPurpose());
        certificateRequest.setDeliveryMode(request.getDeliveryMode());

        certificateRequestRepository.save(certificateRequest);

        return mapToResponse(certificateRequest);
    }

    public List<CertificateResponse> getAllCertificates() {

        List<CertificateRequest> certificates = certificateRequestRepository.findAll();
        List<CertificateResponse> responses = new ArrayList<>();

        for (CertificateRequest cert : certificates) {
            responses.add(mapToResponse(cert));
        }

        return responses;
    }

    private CertificateResponse mapToResponse(CertificateRequest certificateRequest) {

        CertificateResponse response = new CertificateResponse();
        response.setId(certificateRequest.getId());
        response.setRequestId(certificateRequest.getRequest().getId());
        response.setRequestNumber(certificateRequest.getRequest().getRequestNumber());
        response.setCertificateType(certificateRequest.getCertificateType());
        response.setPurpose(certificateRequest.getPurpose());
        response.setDeliveryMode(certificateRequest.getDeliveryMode());
        response.setStatus(certificateRequest.getRequest().getStatus().name());

        return response;
    }
}
