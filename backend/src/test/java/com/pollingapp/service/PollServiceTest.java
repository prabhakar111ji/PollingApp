package com.pollingapp.service;

import com.pollingapp.dto.*;
import com.pollingapp.entity.*;
import com.pollingapp.exception.*;
import com.pollingapp.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PollService using Mockito to isolate business logic
 * from database and security infrastructure.
 */
@ExtendWith(MockitoExtension.class)
class PollServiceTest {

    @Mock private PollRepository pollRepository;
    @Mock private OptionRepository optionRepository;
    @Mock private VoteRepository voteRepository;
    @Mock private LikeRepository likeRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private PollService pollService;

    private User testUser;
    private User otherUser;
    private Poll testPoll;
    private Option optionA;
    private Option optionB;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("john@test.com")
                .firstName("John")
                .lastName("Doe")
                .role("USER")
                .build();

        otherUser = User.builder()
                .id(2L)
                .email("jane@test.com")
                .firstName("Jane")
                .lastName("Smith")
                .role("USER")
                .build();

        optionA = Option.builder()
                .id(1L)
                .title("Java")
                .voteCount(0)
                .build();

        optionB = Option.builder()
                .id(2L)
                .title("Python")
                .voteCount(0)
                .build();

        testPoll = Poll.builder()
                .id(1L)
                .question("Favorite language?")
                .postedDate(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(7))
                .totalVoteCount(0)
                .viewCount(0)
                .user(testUser)
                .options(new ArrayList<>(List.of(optionA, optionB)))
                .votes(new ArrayList<>())
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();

        optionA.setPoll(testPoll);
        optionB.setPoll(testPoll);
    }

    /**
     * Sets up a mock SecurityContext so PollService.getCurrentUser() resolves
     * to the given user. This isolates unit tests from Spring Security infrastructure.
     */
    private void mockAuthenticatedUser(User user) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user.getEmail());
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getPrincipal()).thenReturn(user.getEmail());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ─── Create Poll ─────────────────────────────────────────────

    @Test
    @DisplayName("createPoll — should create poll with valid request")
    void createPoll_validRequest_returnsPollResponse() {
        mockAuthenticatedUser(testUser);

        PollRequest request = new PollRequest();
        request.setQuestion("Best framework?");
        request.setOptions(List.of("Spring", "Django"));
        request.setExpiredAt(LocalDateTime.now().plusDays(3));

        Poll savedPoll = Poll.builder()
                .id(10L)
                .question(request.getQuestion())
                .postedDate(LocalDateTime.now())
                .expiredAt(request.getExpiredAt())
                .totalVoteCount(0)
                .viewCount(0)
                .user(testUser)
                .options(new ArrayList<>())
                .votes(new ArrayList<>())
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();

        when(pollRepository.save(any(Poll.class))).thenReturn(savedPoll);
        when(optionRepository.save(any(Option.class))).thenAnswer(invocation -> {
            Option opt = invocation.getArgument(0);
            opt.setId(100L);
            opt.setPoll(savedPoll);
            return opt;
        });
        when(likeRepository.countByPollId(anyLong())).thenReturn(0);
        when(commentRepository.countByPollId(anyLong())).thenReturn(0);
        when(voteRepository.findByUserIdAndPollId(anyLong(), anyLong())).thenReturn(null);
        when(likeRepository.existsByUserIdAndPollId(anyLong(), anyLong())).thenReturn(false);

        PollResponse response = pollService.createPoll(request);

        assertThat(response).isNotNull();
        assertThat(response.getQuestion()).isEqualTo("Best framework?");
        assertThat(response.getTotalVoteCount()).isEqualTo(0);
        assertThat(response.getCreatorName()).isEqualTo("John Doe");
        verify(pollRepository).save(any(Poll.class));
        verify(optionRepository, times(2)).save(any(Option.class));
    }

    @Test
    @DisplayName("createPoll — should reject past expiration date")
    void createPoll_pastExpiration_throwsBadRequest() {
        mockAuthenticatedUser(testUser);

        PollRequest request = new PollRequest();
        request.setQuestion("Too late?");
        request.setOptions(List.of("Yes", "No"));
        request.setExpiredAt(LocalDateTime.now().minusDays(1));

        assertThatThrownBy(() -> pollService.createPoll(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("future");
    }

    // ─── Vote ────────────────────────────────────────────────────

    @Test
    @DisplayName("vote — should successfully cast a vote")
    void vote_validRequest_updatesCountsAndReturns() {
        mockAuthenticatedUser(otherUser);

        VoteRequest request = new VoteRequest(1L, 1L);
        when(pollRepository.findById(1L)).thenReturn(Optional.of(testPoll));
        when(voteRepository.existsByUserIdAndPollId(otherUser.getId(), testPoll.getId())).thenReturn(false);
        when(optionRepository.findById(1L)).thenReturn(Optional.of(optionA));
        when(voteRepository.save(any(Vote.class))).thenAnswer(i -> i.getArgument(0));
        when(optionRepository.save(any(Option.class))).thenAnswer(i -> i.getArgument(0));
        when(pollRepository.save(any(Poll.class))).thenAnswer(i -> i.getArgument(0));
        when(likeRepository.countByPollId(anyLong())).thenReturn(0);
        when(commentRepository.countByPollId(anyLong())).thenReturn(0);

        // After voting, the vote should exist for mapping
        Vote newVote = Vote.builder().user(otherUser).poll(testPoll).option(optionA).build();
        when(voteRepository.findByUserIdAndPollId(otherUser.getId(), testPoll.getId())).thenReturn(newVote);
        when(likeRepository.existsByUserIdAndPollId(otherUser.getId(), testPoll.getId())).thenReturn(false);

        PollResponse response = pollService.vote(request);

        assertThat(response).isNotNull();
        assertThat(response.getTotalVoteCount()).isEqualTo(1);
        assertThat(optionA.getVoteCount()).isEqualTo(1);
        verify(voteRepository).save(any(Vote.class));
    }

    @Test
    @DisplayName("vote — should reject duplicate vote")
    void vote_duplicateVote_throwsConflict() {
        mockAuthenticatedUser(otherUser);

        VoteRequest request = new VoteRequest(1L, 1L);
        when(pollRepository.findById(1L)).thenReturn(Optional.of(testPoll));
        when(voteRepository.existsByUserIdAndPollId(otherUser.getId(), testPoll.getId())).thenReturn(true);

        assertThatThrownBy(() -> pollService.vote(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already voted");
    }

    @Test
    @DisplayName("vote — should reject vote on expired poll")
    void vote_expiredPoll_throwsBadRequest() {
        mockAuthenticatedUser(otherUser);
        testPoll.setExpiredAt(LocalDateTime.now().minusDays(1));

        VoteRequest request = new VoteRequest(1L, 1L);
        when(pollRepository.findById(1L)).thenReturn(Optional.of(testPoll));

        assertThatThrownBy(() -> pollService.vote(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("expired");
    }

    @Test
    @DisplayName("vote — should reject option not belonging to poll")
    void vote_wrongOption_throwsBadRequest() {
        mockAuthenticatedUser(otherUser);

        Poll otherPoll = Poll.builder().id(99L).build();
        Option wrongOption = Option.builder().id(5L).title("Wrong").poll(otherPoll).build();

        VoteRequest request = new VoteRequest(1L, 5L);
        when(pollRepository.findById(1L)).thenReturn(Optional.of(testPoll));
        when(voteRepository.existsByUserIdAndPollId(otherUser.getId(), testPoll.getId())).thenReturn(false);
        when(optionRepository.findById(5L)).thenReturn(Optional.of(wrongOption));

        assertThatThrownBy(() -> pollService.vote(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("does not belong");
    }

    // ─── Toggle Like ─────────────────────────────────────────────

    @Test
    @DisplayName("toggleLike — should add like when not liked")
    void toggleLike_notLiked_addsLike() {
        mockAuthenticatedUser(testUser);

        when(pollRepository.findById(1L)).thenReturn(Optional.of(testPoll));
        when(likeRepository.findByUserIdAndPollId(testUser.getId(), testPoll.getId())).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenAnswer(i -> i.getArgument(0));
        when(likeRepository.countByPollId(testPoll.getId())).thenReturn(1);
        when(commentRepository.countByPollId(testPoll.getId())).thenReturn(0);
        when(voteRepository.findByUserIdAndPollId(testUser.getId(), testPoll.getId())).thenReturn(null);
        when(likeRepository.existsByUserIdAndPollId(testUser.getId(), testPoll.getId())).thenReturn(true);

        PollResponse response = pollService.toggleLike(1L);

        assertThat(response.getLikesCount()).isEqualTo(1);
        assertThat(response.isHasLiked()).isTrue();
        verify(likeRepository).save(any(Like.class));
        verify(likeRepository, never()).delete(any(Like.class));
    }

    @Test
    @DisplayName("toggleLike — should remove like when already liked")
    void toggleLike_alreadyLiked_removesLike() {
        mockAuthenticatedUser(testUser);

        Like existingLike = Like.builder().id(1L).user(testUser).poll(testPoll).build();
        when(pollRepository.findById(1L)).thenReturn(Optional.of(testPoll));
        when(likeRepository.findByUserIdAndPollId(testUser.getId(), testPoll.getId())).thenReturn(Optional.of(existingLike));
        when(likeRepository.countByPollId(testPoll.getId())).thenReturn(0);
        when(commentRepository.countByPollId(testPoll.getId())).thenReturn(0);
        when(voteRepository.findByUserIdAndPollId(testUser.getId(), testPoll.getId())).thenReturn(null);
        when(likeRepository.existsByUserIdAndPollId(testUser.getId(), testPoll.getId())).thenReturn(false);

        PollResponse response = pollService.toggleLike(1L);

        assertThat(response.getLikesCount()).isEqualTo(0);
        assertThat(response.isHasLiked()).isFalse();
        verify(likeRepository).delete(existingLike);
        verify(likeRepository, never()).save(any(Like.class));
    }

    // ─── Delete Poll ─────────────────────────────────────────────

    @Test
    @DisplayName("deletePoll — owner should delete successfully")
    void deletePoll_owner_deletes() {
        mockAuthenticatedUser(testUser);

        when(pollRepository.findById(1L)).thenReturn(Optional.of(testPoll));

        pollService.deletePoll(1L);

        verify(pollRepository).delete(testPoll);
    }

    @Test
    @DisplayName("deletePoll — non-owner should be forbidden")
    void deletePoll_nonOwner_throwsForbidden() {
        mockAuthenticatedUser(otherUser);

        when(pollRepository.findById(1L)).thenReturn(Optional.of(testPoll));

        assertThatThrownBy(() -> pollService.deletePoll(1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("own polls");
    }

    @Test
    @DisplayName("deletePoll — non-existent poll should return 404")
    void deletePoll_notFound_throwsNotFound() {
        mockAuthenticatedUser(testUser);

        when(pollRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pollService.deletePoll(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── Add Comment ─────────────────────────────────────────────

    @Test
    @DisplayName("addComment — should create comment successfully")
    void addComment_validRequest_returnsCommentDTO() {
        mockAuthenticatedUser(testUser);

        CommentRequest request = new CommentRequest(1L, "Great poll!");
        when(pollRepository.findById(1L)).thenReturn(Optional.of(testPoll));

        Comment savedComment = Comment.builder()
                .id(1L)
                .content("Great poll!")
                .createdAt(LocalDateTime.now())
                .user(testUser)
                .poll(testPoll)
                .build();
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDTO result = pollService.addComment(request);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Great poll!");
        assertThat(result.getAuthorName()).isEqualTo("John Doe");
        verify(commentRepository).save(any(Comment.class));
    }
}
