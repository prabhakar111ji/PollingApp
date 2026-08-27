package com.pollingapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pollingapp.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PollingAppIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private String userToken;
    private String user2Token;
    private Long createdPollId;
    private Long optionId1;
    private Long optionId2;

    @Test
    @Order(1)
    void testSignupSuccess() throws Exception {
        SignupRequest request = new SignupRequest("John", "Doe", "john@test.com", "password123");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    @Order(2)
    void testDuplicateSignup() throws Exception {
        SignupRequest request = new SignupRequest("John", "Doe", "john@test.com", "password123");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("john@test.com", "password123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("john@test.com"))
                .andReturn();

        AuthenticationResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthenticationResponse.class);
        userToken = response.getToken();
    }

    @Test
    @Order(4)
    void testInvalidLogin() throws Exception {
        LoginRequest request = new LoginRequest("john@test.com", "wrongpassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(5)
    void testCreatePoll() throws Exception {
        PollRequest request = new PollRequest();
        request.setQuestion("What is your favorite language?");
        request.setOptions(Arrays.asList("Java", "Python", "JavaScript"));
        request.setExpiredAt(LocalDateTime.now().plusDays(7));

        MvcResult result = mockMvc.perform(post("/api/user/poll")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.question").value("What is your favorite language?"))
                .andExpect(jsonPath("$.options.length()").value(3))
                .andReturn();

        PollResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), PollResponse.class);
        createdPollId = response.getId();
        optionId1 = response.getOptions().get(0).getId();
        optionId2 = response.getOptions().get(1).getId();
    }

    @Test
    @Order(6)
    void testGetAllPolls() throws Exception {
        mockMvc.perform(get("/api/user/poll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @Order(7)
    void testSignupSecondUser() throws Exception {
        SignupRequest request = new SignupRequest("Jane", "Smith", "jane@test.com", "password123");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest("jane@test.com", "password123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthenticationResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthenticationResponse.class);
        user2Token = response.getToken();
    }

    @Test
    @Order(8)
    void testVoteSuccess() throws Exception {
        VoteRequest request = new VoteRequest(createdPollId, optionId1);
        mockMvc.perform(post("/api/user/poll/vote")
                        .header("Authorization", "Bearer " + user2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVoteCount").value(1))
                .andExpect(jsonPath("$.hasVoted").value(true));
    }

    @Test
    @Order(9)
    void testDuplicateVoteRejection() throws Exception {
        VoteRequest request = new VoteRequest(createdPollId, optionId2);
        mockMvc.perform(post("/api/user/poll/vote")
                        .header("Authorization", "Bearer " + user2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(10)
    void testExpiredPollRejection() throws Exception {
        // Create an expired poll
        PollRequest request = new PollRequest();
        request.setQuestion("Expired poll?");
        request.setOptions(Arrays.asList("Yes", "No"));
        request.setExpiredAt(LocalDateTime.now().plusSeconds(1));

        MvcResult result = mockMvc.perform(post("/api/user/poll")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        PollResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), PollResponse.class);

        // Wait for it to expire
        Thread.sleep(2000);

        VoteRequest voteRequest = new VoteRequest(response.getId(), response.getOptions().get(0).getId());
        mockMvc.perform(post("/api/user/poll/vote")
                        .header("Authorization", "Bearer " + user2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(11)
    void testUnauthorizedDeleteRejection() throws Exception {
        mockMvc.perform(delete("/api/user/poll/" + createdPollId)
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(12)
    void testOwnerDeleteSuccess() throws Exception {
        mockMvc.perform(delete("/api/user/poll/" + createdPollId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Poll deleted successfully"));
    }

    @Test
    @Order(13)
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
