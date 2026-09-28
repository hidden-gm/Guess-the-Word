package com.guesswordgame.service;

import com.guesswordgame.dto.GameDtos;
import com.guesswordgame.entity.User;
import com.guesswordgame.entity.Word;
import com.guesswordgame.repository.GameRepository;
import com.guesswordgame.repository.UserRepository;
import com.guesswordgame.repository.WordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final WordRepository wordRepository;

    public AdminService(UserRepository userRepository, GameRepository gameRepository, WordRepository wordRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.wordRepository = wordRepository;
    }

    public GameDtos.DailyReport dayReport(LocalDate date) {
        return new GameDtos.DailyReport(
                gameRepository.countDistinctUsersByDate(date),
                gameRepository.countWonGamesByDate(date));
    }

    @Transactional(readOnly = true)
    public List<GameDtos.UserDailyReport> userReport(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return gameRepository.getUserDailyReport(user).stream()
                .map(row -> new GameDtos.UserDailyReport(row.getGameDate().toString(), row.getTried(), row.getCorrect()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GameDtos.WordResponse> words() {
        return wordRepository.findAll().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .map(w -> new GameDtos.WordResponse(w.getId(), w.getValue()))
                .toList();
    }

    @Transactional
    public GameDtos.WordResponse addWord(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase();
        if (!normalized.matches("^[A-Z]{5}$")) {
            throw new IllegalArgumentException("Word must be exactly 5 uppercase letters.");
        }
        if (wordRepository.findByValue(normalized).isPresent()) {
            throw new IllegalArgumentException("Word already exists.");
        }
        Word saved = wordRepository.save(new Word(normalized));
        return new GameDtos.WordResponse(saved.getId(), saved.getValue());
    }

    @Transactional
    public void deleteWord(Long id) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Word not found."));
        if (wordRepository.count() <= 1) {
            throw new IllegalStateException("At least one word must remain configured.");
        }
        if (gameRepository.countByWord(word) > 0) {
            throw new IllegalStateException("This word is already attached to a game and cannot be deleted.");
        }
        wordRepository.delete(word);
    }
}
