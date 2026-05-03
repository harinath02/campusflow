package com.campusflow.certificate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCertificateRequest {

    @NotNull
    private Long requesterId;

    @NotBlank
    private String certificateType;

    @NotBlank
    private String purpose;

    @NotBlank
    private String deliveryMode;
}
