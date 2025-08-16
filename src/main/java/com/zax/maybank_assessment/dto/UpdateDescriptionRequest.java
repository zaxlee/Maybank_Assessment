package com.zax.maybank_assessment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/** Request body for updating description. */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateDescriptionRequest {
    @NotBlank
    @Size(max = 255)
    private String description;
}