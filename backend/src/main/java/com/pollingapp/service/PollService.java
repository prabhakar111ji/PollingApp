package com.pollingapp.service;

import com.pollingapp.dto.*;
import com.pollingapp.entity.*;
import com.pollingapp.exception.*;
import com.pollingapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PollService {

    private final PollRepository pollRepository;
    private final OptionRepository optionRepository;
    private final VoteRepository voteRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AiService aiService;

    // ─── Helpers ───────────────────────────────────────────────

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private User getCurrentUserOrNull() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return null;
            }
            String email = auth.getName();
            return userRepository.findByEmail(email).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private List<OptionDTO> mapOptions(Poll poll) {
        return poll.getOptions().stream().map(option -> {
            double percentage = poll.getTotalVoteCount() == 0
                    ? 0.0
                    : Math.round((option.getVoteCount() * 100.0 / poll.getTotalVoteCount()) * 10.0) / 10.0;
            return OptionDTO.builder()
                    .id(option.getId())
                    .title(option.getTitle())
                    .voteCount(option.getVoteCount())
                    .percentage(percentage)
                    .build();
        }).collect(Collectors.toList());
    }

    private PollResponse mapToPollResponse(Poll poll, User currentUser) {
        Long selectedOptionId = null;
        boolean hasVoted = false;
        boolean hasLiked = false;

        if (currentUser != null) {
            Vote vote = voteRepository.findByUserIdAndPollId(currentUser.getId(), poll.getId());
            if (vote != null) {
                hasVoted = true;
                selectedOptionId = vote.getOption().getId();
            }
            hasLiked = likeRepository.existsByUserIdAndPollId(currentUser.getId(), poll.getId());
        }

        return PollResponse.builder()
                .id(poll.getId())
                .question(poll.getQuestion())
                .postedDate(poll.getPostedDate())
                .expiredAt(poll.getExpiredAt())
                .expired(poll.isExpired())
                .totalVoteCount(poll.getTotalVoteCount())
                .creatorName(poll.getUser().getFirstName() + " " + poll.getUser().getLastName())
                .creatorId(poll.getUser().getId())
                .options(mapOptions(poll))
                .selectedOptionId(selectedOptionId)
                .hasVoted(hasVoted)
                .likesCount(likeRepository.countByPollId(poll.getId()))
                .commentsCount(commentRepository.countByPollId(poll.getId()))
                .viewCount(poll.getViewCount())
                .hasLiked(hasLiked)
                .build();
    }

    // ─── Poll CRUD ────────────────────────────────────────────

    @Transactional
    public PollResponse createPoll(PollRequest request) {
        User user = getCurrentUser();

        if (request.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Expiration date must be in the future");
        }

        Poll poll = Poll.builder()
                .question(request.getQuestion())
                .postedDate(LocalDateTime.now())
                .expiredAt(request.getExpiredAt())
                .totalVoteCount(0)
                .user(user)
                .build();

        Poll savedPoll = pollRepository.save(poll);

        for (String optionTitle : request.getOptions()) {
            if (optionTitle == null || optionTitle.trim().isEmpty()) {
                throw new BadRequestException("Option title cannot be empty");
            }
            Option option = Option.builder()
                    .title(optionTitle.trim())
                    .voteCount(0)
                    .poll(savedPoll)
                    .build();
            optionRepository.save(option);
            savedPoll.getOptions().add(option);
        }

        return mapToPollResponse(savedPoll, user);
    }

    public List<PollResponse> getAllPolls() {
        User currentUser = getCurrentUserOrNull();
        return pollRepository.findAllByOrderByPostedDateDesc().stream()
                .map(poll -> mapToPollResponse(poll, currentUser))
                .collect(Collectors.toList());
    }

    public Page<PollResponse> getAllPollsPaginated(Pageable pageable) {
        User currentUser = getCurrentUserOrNull();
        return pollRepository.findAllByOrderByPostedDateDesc(pageable)
                .map(poll -> mapToPollResponse(poll, currentUser));
    }

    @Transactional
    public PollDetailsDTO getPollById(Long id) {
        User currentUser = getCurrentUserOrNull();
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found with id: " + id));

        // Increment view count
        poll.setViewCount(poll.getViewCount() + 1);

        Long selectedOptionId = null;
        boolean hasVoted = false;
        boolean hasLiked = false;

        if (currentUser != null) {
            Vote vote = voteRepository.findByUserIdAndPollId(currentUser.getId(), poll.getId());
            if (vote != null) {
                hasVoted = true;
                selectedOptionId = vote.getOption().getId();
            }
            hasLiked = likeRepository.existsByUserIdAndPollId(currentUser.getId(), poll.getId());
        }

        List<CommentDTO> comments = commentRepository.findByPollIdOrderByCreatedAtDesc(poll.getId())
                .stream().map(comment -> CommentDTO.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .authorName(comment.getUser().getFirstName() + " " + comment.getUser().getLastName())
                        .authorId(comment.getUser().getId())
                        .build())
                .collect(Collectors.toList());

        return PollDetailsDTO.builder()
                .id(poll.getId())
                .question(poll.getQuestion())
                .postedDate(poll.getPostedDate())
                .expiredAt(poll.getExpiredAt())
                .expired(poll.isExpired())
                .totalVoteCount(poll.getTotalVoteCount())
                .creatorName(poll.getUser().getFirstName() + " " + poll.getUser().getLastName())
                .creatorId(poll.getUser().getId())
                .options(mapOptions(poll))
                .selectedOptionId(selectedOptionId)
                .hasVoted(hasVoted)
                .likesCount(likeRepository.countByPollId(poll.getId()))
                .commentsCount(commentRepository.countByPollId(poll.getId()))
                .viewCount(poll.getViewCount())
                .hasLiked(hasLiked)
                .comments(comments)
                .build();
    }

    public List<PollResponse> getMyPolls() {
        User user = getCurrentUser();
        return pollRepository.findByUserOrderByPostedDateDesc(user).stream()
                .map(poll -> mapToPollResponse(poll, user))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePoll(Long id) {
        User user = getCurrentUser();
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found with id: " + id));

        if (!poll.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You can only delete your own polls");
        }

        // Clear children in correct order: votes reference options via FK,
        // so votes must be deleted before options to avoid constraint violations
        poll.getComments().clear();
        poll.getLikes().clear();
        poll.getVotes().clear();
        pollRepository.saveAndFlush(poll);

        poll.getOptions().clear();
        pollRepository.saveAndFlush(poll);

        pollRepository.delete(poll);
    }

    // ─── Voting ───────────────────────────────────────────────

    @Transactional
    public PollResponse vote(VoteRequest request) {
        User user = getCurrentUser();

        Poll poll = pollRepository.findById(request.getPollId())
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found"));

        if (poll.isExpired()) {
            throw new BadRequestException("Poll has expired and cannot be voted on");
        }

        if (voteRepository.existsByUserIdAndPollId(user.getId(), poll.getId())) {
            throw new ConflictException("User has already voted on this poll");
        }

        Option option = optionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Option not found"));

        if (!option.getPoll().getId().equals(poll.getId())) {
            throw new BadRequestException("Option does not belong to the specified poll");
        }

        Vote vote = Vote.builder()
                .postedDate(LocalDateTime.now())
                .user(user)
                .poll(poll)
                .option(option)
                .build();
        voteRepository.save(vote);

        option.setVoteCount(option.getVoteCount() + 1);
        optionRepository.save(option);

        poll.setTotalVoteCount(poll.getTotalVoteCount() + 1);
        pollRepository.save(poll);

        return mapToPollResponse(poll, user);
    }

    // ─── Likes (Toggle) ──────────────────────────────────────

    @Transactional
    public PollResponse toggleLike(Long pollId) {
        User user = getCurrentUser();
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found"));

        var existingLike = likeRepository.findByUserIdAndPollId(user.getId(), poll.getId());
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like like = Like.builder()
                    .user(user)
                    .poll(poll)
                    .build();
            likeRepository.save(like);
        }

        return mapToPollResponse(poll, user);
    }

    // ─── Comments ────────────────────────────────────────────

    @Transactional
    public CommentDTO addComment(CommentRequest request) {
        User user = getCurrentUser();
        Poll poll = pollRepository.findById(request.getPollId())
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .user(user)
                .poll(poll)
                .build();

        Comment saved = commentRepository.save(comment);

        return CommentDTO.builder()
                .id(saved.getId())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .authorName(user.getFirstName() + " " + user.getLastName())
                .authorId(user.getId())
                .build();
    }

    // ─── AI Summary ──────────────────────────────────────────────

    public AiSummaryResponse getAiSummary(Long pollId) {
        PollDetailsDTO poll = getPollById(pollId);
        String summary = aiService.generatePollSummary(poll);
        return AiSummaryResponse.builder()
                .summary(summary)
                .aiGenerated(aiService.isConfigured())
                .model(aiService.isConfigured() ? "gpt-3.5-turbo" : "rule-based")
                .build();
    }
}
