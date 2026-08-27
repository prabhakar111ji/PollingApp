package com.pollingapp.repository;

import com.pollingapp.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndPollId(Long userId, Long pollId);
    Optional<Like> findByUserIdAndPollId(Long userId, Long pollId);
    int countByPollId(Long pollId);
}
