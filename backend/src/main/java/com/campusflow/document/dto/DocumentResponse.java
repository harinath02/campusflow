package com.campusflow.document.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DocumentResponse {

    private Long id;
    private Long requestId;
    private String fileName;
    private String contentType;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}
