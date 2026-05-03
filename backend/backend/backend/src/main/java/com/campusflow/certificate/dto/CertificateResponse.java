package com.campusflow.certificate.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertificateResponse {

    private Long id;
    private Long requestId;
    private String requestNumber;
    private String certificateType;
    private String purpose;
    private String deliveryMode;
    private String status;
}
