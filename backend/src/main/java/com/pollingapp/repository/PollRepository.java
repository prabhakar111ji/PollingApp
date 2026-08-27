package com.pollingapp.repository;

import com.pollingapp.entity.Poll;
import com.pollingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findAllByOrderByPostedDateDesc();
    List<Poll> findByUserOrderByPostedDateDesc(User user);
}
