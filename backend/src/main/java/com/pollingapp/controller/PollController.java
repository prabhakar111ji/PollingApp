package com.pollingapp.controller;

import com.pollingapp.dto.*;
import com.pollingapp.service.PollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/poll")
@RequiredArgsConstructor
@Tag(name = "Polls", description = "Poll management and voting API")
public class PollController {

    private final PollService pollService;

    @PostMapping
    @Operation(summary = "Create a new poll", description = "Create a poll with a question, options, and expiration date")
    public ResponseEntity<PollResponse> createPoll(@Valid @RequestBody PollRequest request) {
        PollResponse response = pollService.createPoll(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all polls (paginated)", description = "Fetch polls with optional pagination. Use page & size params for paginated results.")
    public ResponseEntity<?> getAllPolls(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        if (page != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<PollResponse> pagedResult = pollService.getAllPollsPaginated(pageable);
            return ResponseEntity.ok(pagedResult);
        }
        // Backward compatible: return full list if no page param
        return ResponseEntity.ok(pollService.getAllPolls());
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "Get poll details", description = "Get detailed poll information including comments")
    public ResponseEntity<PollDetailsDTO> getPollById(@PathVariable Long id) {
        return ResponseEntity.ok(pollService.getPollById(id));
    }

    @GetMapping("/my")
    @Operation(summary = "Get my polls", description = "Get all polls created by the authenticated user")
    public ResponseEntity<?> getMyPolls() {
        return ResponseEntity.ok(pollService.getMyPolls());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a poll", description = "Delete a poll (owner only)")
    public ResponseEntity<Map<String, String>> deletePoll(@PathVariable Long id) {
        pollService.deletePoll(id);
        return ResponseEntity.ok(Map.of("message", "Poll deleted successfully"));
    }

    @PostMapping("/vote")
    @Operation(summary = "Vote on a poll", description = "Cast a vote on a poll (one vote per user)")
    public ResponseEntity<PollResponse> vote(@Valid @RequestBody VoteRequest request) {
        return ResponseEntity.ok(pollService.vote(request));
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "Toggle like", description = "Like or unlike a poll")
    public ResponseEntity<PollResponse> toggleLike(@PathVariable Long id) {
        return ResponseEntity.ok(pollService.toggleLike(id));
    }

    @PostMapping("/comment")
    @Operation(summary = "Add a comment", description = "Add a comment to a poll")
    public ResponseEntity<CommentDTO> addComment(@Valid @RequestBody CommentRequest request) {
        CommentDTO comment = pollService.addComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/{id}/ai-summary")
    @Operation(summary = "Get AI poll summary", description = "Generate an AI-powered summary of poll results and community sentiment")
    public ResponseEntity<AiSummaryResponse> getAiSummary(@PathVariable Long id) {
        return ResponseEntity.ok(pollService.getAiSummary(id));
    }
}
