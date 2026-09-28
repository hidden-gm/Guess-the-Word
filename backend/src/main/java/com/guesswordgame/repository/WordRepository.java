package com.guesswordgame.repository;

import com.guesswordgame.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long> {
    Optional<Word> findByValue(String value);
}
