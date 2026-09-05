package com.pollingapp.service;

import com.pollingapp.dto.OptionDTO;
import com.pollingapp.dto.PollDetailsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for AI-powered poll analysis using OpenAI's Chat Completions API.
 * Generates natural language summaries of poll results and community sentiment.
 */
@Service
@Slf4j
public class AiService {

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${openai.api.model:gpt-3.5-turbo}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Checks whether the OpenAI API key is configured.
     */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    /**
     * Generates an AI summary of a poll's results and comments.
     *
     * @param poll the poll details including options, votes, and comments
     * @return AI-generated summary string
     */
    public String generatePollSummary(PollDetailsDTO poll) {
        if (!isConfigured()) {
            return generateFallbackSummary(poll);
        }

        try {
            String prompt = buildPrompt(poll);

            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content",
                            "You are a concise data analyst. Summarize poll results in 2-3 sentences. " +
                            "Include the winning option, vote distribution, and overall sentiment from comments if available. " +
                            "Be factual and neutral."),
                    Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("max_tokens", 200);
            requestBody.put("temperature", 0.7);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    log.info("AI summary generated for poll id={}", poll.getId());
                    return content.trim();
                }
            }

            log.warn("Unexpected OpenAI response for poll id={}", poll.getId());
            return generateFallbackSummary(poll);

        } catch (Exception e) {
            log.error("OpenAI API call failed for poll id={}: {}", poll.getId(), e.getMessage());
            return generateFallbackSummary(poll);
        }
    }

    /**
     * Builds a structured prompt from poll data for the AI model.
     */
    private String buildPrompt(PollDetailsDTO poll) {
        StringBuilder sb = new StringBuilder();
        sb.append("Poll Question: \"").append(poll.getQuestion()).append("\"\n");
        sb.append("Status: ").append(poll.isExpired() ? "Closed" : "Active").append("\n");
        sb.append("Total Votes: ").append(poll.getTotalVoteCount()).append("\n\n");

        sb.append("Results:\n");
        for (OptionDTO option : poll.getOptions()) {
            sb.append("- ").append(option.getTitle())
              .append(": ").append(option.getVoteCount()).append(" votes")
              .append(" (").append(option.getPercentage()).append("%)\n");
        }

        if (poll.getComments() != null && !poll.getComments().isEmpty()) {
            sb.append("\nRecent Comments:\n");
            poll.getComments().stream()
                    .limit(5)
                    .forEach(c -> sb.append("- \"").append(c.getContent()).append("\"\n"));
        }

        sb.append("\nSummarize this poll's results and community sentiment.");
        return sb.toString();
    }

    /**
     * Generates a rule-based fallback summary when the OpenAI API is not available.
     * This ensures the feature works even without an API key configured.
     */
    private String generateFallbackSummary(PollDetailsDTO poll) {
        if (poll.getTotalVoteCount() == 0) {
            return String.format("The poll \"%s\" has not received any votes yet. %s",
                    poll.getQuestion(),
                    poll.isExpired() ? "The poll has expired." : "Voting is still open.");
        }

        // Find the winning option
        OptionDTO winner = poll.getOptions().stream()
                .max(Comparator.comparingInt(OptionDTO::getVoteCount))
                .orElse(null);

        if (winner == null) {
            return "Unable to determine poll results.";
        }

        // Check if it's a close race
        List<OptionDTO> sorted = poll.getOptions().stream()
                .sorted(Comparator.comparingInt(OptionDTO::getVoteCount).reversed())
                .collect(Collectors.toList());

        String summary;
        if (sorted.size() >= 2 && sorted.get(0).getVoteCount().equals(sorted.get(1).getVoteCount())) {
            summary = String.format(
                    "The poll \"%s\" shows a tie between \"%s\" and \"%s\", each with %d votes (%.1f%%). " +
                    "Out of %d total votes, the community appears evenly divided.",
                    poll.getQuestion(), sorted.get(0).getTitle(), sorted.get(1).getTitle(),
                    sorted.get(0).getVoteCount(), sorted.get(0).getPercentage(),
                    poll.getTotalVoteCount());
        } else {
            summary = String.format(
                    "In the poll \"%s\", \"%s\" leads with %d votes (%.1f%%) out of %d total votes. " +
                    "The runner-up is \"%s\" with %d votes (%.1f%%).",
                    poll.getQuestion(), winner.getTitle(), winner.getVoteCount(), winner.getPercentage(),
                    poll.getTotalVoteCount(),
                    sorted.size() >= 2 ? sorted.get(1).getTitle() : "N/A",
                    sorted.size() >= 2 ? sorted.get(1).getVoteCount() : 0,
                    sorted.size() >= 2 ? sorted.get(1).getPercentage() : 0.0);
        }

        if (poll.getComments() != null && !poll.getComments().isEmpty()) {
            summary += String.format(" The poll has generated %d comment%s from the community.",
                    poll.getCommentsCount(), poll.getCommentsCount() == 1 ? "" : "s");
        }

        if (poll.isExpired()) {
            summary += " This poll is now closed.";
        }

        return summary;
    }
}
