package com.pollingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollDetailsDTO {
    private Long id;
    private String question;
    private LocalDateTime postedDate;
    private LocalDateTime expiredAt;
    private boolean expired;
    private Integer totalVoteCount;
    private String creatorName;
    private Long creatorId;
    private List<OptionDTO> options;
    private Long selectedOptionId;
    private boolean hasVoted;
    private int likesCount;
    private int commentsCount;
    private boolean hasLiked;
    private List<CommentDTO> comments;
}
