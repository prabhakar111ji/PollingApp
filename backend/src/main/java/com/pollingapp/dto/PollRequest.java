package com.pollingapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollRequest {

    @NotBlank(message = "Question is required")
    private String question;

    @NotNull(message = "Options are required")
    @Size(min = 2, message = "At least 2 options are required")
    private List<String> options;

    @NotNull(message = "Expiration date is required")
    private LocalDateTime expiredAt;
}
