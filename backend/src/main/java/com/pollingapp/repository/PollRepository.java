package com.pollingapp.repository;

import com.pollingapp.entity.Poll;
import com.pollingapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findAllByOrderByPostedDateDesc();
    Page<Poll> findAllByOrderByPostedDateDesc(Pageable pageable);
    List<Poll> findByUserOrderByPostedDateDesc(User user);
}
