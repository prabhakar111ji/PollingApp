package com.pollingapp.controller;

import com.pollingapp.dto.*;
import com.pollingapp.service.PollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/poll")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    @PostMapping
    public ResponseEntity<PollResponse> createPoll(@Valid @RequestBody PollRequest request) {
        PollResponse response = pollService.createPoll(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PollResponse>> getAllPolls() {
        return ResponseEntity.ok(pollService.getAllPolls());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<PollDetailsDTO> getPollById(@PathVariable Long id) {
        return ResponseEntity.ok(pollService.getPollById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<PollResponse>> getMyPolls() {
        return ResponseEntity.ok(pollService.getMyPolls());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePoll(@PathVariable Long id) {
        pollService.deletePoll(id);
        return ResponseEntity.ok(Map.of("message", "Poll deleted successfully"));
    }

    @PostMapping("/vote")
    public ResponseEntity<PollResponse> vote(@Valid @RequestBody VoteRequest request) {
        return ResponseEntity.ok(pollService.vote(request));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<PollResponse> toggleLike(@PathVariable Long id) {
        return ResponseEntity.ok(pollService.toggleLike(id));
    }

    @PostMapping("/comment")
    public ResponseEntity<CommentDTO> addComment(@Valid @RequestBody CommentRequest request) {
        CommentDTO comment = pollService.addComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
}
