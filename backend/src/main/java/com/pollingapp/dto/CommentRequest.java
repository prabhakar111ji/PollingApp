package com.pollingapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotNull(message = "Poll ID is required")
    private Long pollId;

    @NotBlank(message = "Comment cannot be blank")
    @Size(max = 1000, message = "Comment must be less than 1000 characters")
    private String content;
}
