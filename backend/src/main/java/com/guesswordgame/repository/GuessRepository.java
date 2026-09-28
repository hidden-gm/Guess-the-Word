package com.guesswordgame.repository;

import com.guesswordgame.entity.Guess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuessRepository extends JpaRepository<Guess, Long> {
    List<Guess> findByGameOrderByIdAsc(com.guesswordgame.entity.Game game);
}
