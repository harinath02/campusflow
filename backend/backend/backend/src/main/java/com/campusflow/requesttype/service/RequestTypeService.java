package com.campusflow.requesttype.service;

import com.campusflow.exception.ResourceAlreadyExistsException;
import com.campusflow.exception.ResourceNotFoundException;
import com.campusflow.requesttype.dto.CreateRequestTypeRequest;
import com.campusflow.requesttype.dto.RequestTypeResponse;
import com.campusflow.requesttype.entity.RequestType;
import com.campusflow.requesttype.repository.RequestTypeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RequestTypeService {

    private final RequestTypeRepository requestTypeRepository;

    public RequestTypeService(RequestTypeRepository requestTypeRepository) {
        this.requestTypeRepository = requestTypeRepository;
    }

    public RequestTypeResponse createRequestType(CreateRequestTypeRequest request) {

        if (requestTypeRepository.existsByCode(request.getCode())) {
            throw new ResourceAlreadyExistsException("Request type code already exists");
        }

        RequestType requestType = new RequestType();
        requestType.setName(request.getName());
        requestType.setCode(request.getCode());
        requestType.setDescription(request.getDescription());
        requestType.setActive(true);

        RequestType saved = requestTypeRepository.save(requestType);

        return mapToResponse(saved);
    }

    public List<RequestTypeResponse> getAllRequestTypes() {

        List<RequestType> requestTypes = requestTypeRepository.findAll();

        List<RequestTypeResponse> responses = new ArrayList<>();

        for (RequestType requestType : requestTypes) {
            responses.add(mapToResponse(requestType));
        }

        return responses;
    }

    public RequestTypeResponse getRequestTypeById(Long id) {

        RequestType requestType = requestTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request type not found"));

        return mapToResponse(requestType);
    }

    public RequestTypeResponse updateRequestType(Long id, CreateRequestTypeRequest request) {

        RequestType requestType = requestTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request type not found"));

        if (!requestType.getCode().equals(request.getCode()) &&
            requestTypeRepository.existsByCode(request.getCode())) {
            throw new ResourceAlreadyExistsException("Request type code already exists");
        }

        requestType.setName(request.getName());
        requestType.setCode(request.getCode());
        requestType.setDescription(request.getDescription());

        RequestType saved = requestTypeRepository.save(requestType);

        return mapToResponse(saved);
    }

    public void deactivateRequestType(Long id) {

        RequestType requestType = requestTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request type not found"));

        requestType.setActive(false);
        requestTypeRepository.save(requestType);
    }

    private RequestTypeResponse mapToResponse(RequestType requestType) {
        RequestTypeResponse response = new RequestTypeResponse();
        response.setId(requestType.getId());
        response.setName(requestType.getName());
        response.setCode(requestType.getCode());
        response.setDescription(requestType.getDescription());
        response.setActive(requestType.getActive());
        return response;
    }
}