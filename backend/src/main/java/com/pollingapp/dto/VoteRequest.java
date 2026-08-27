package com.pollingapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {

    @NotNull(message = "Poll ID is required")
    private Long pollId;

    @NotNull(message = "Option ID is required")
    private Long optionId;
}
