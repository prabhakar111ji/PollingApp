package com.pollingapp.repository;

import com.pollingapp.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByUserIdAndPollId(Long userId, Long pollId);
    Vote findByUserIdAndPollId(Long userId, Long pollId);
}
