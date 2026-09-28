package com.guesswordgame.service;

import com.guesswordgame.dto.GameDtos;
import com.guesswordgame.entity.Game;
import com.guesswordgame.entity.Guess;
import com.guesswordgame.entity.User;
import com.guesswordgame.entity.Word;
import com.guesswordgame.repository.GameRepository;
import com.guesswordgame.repository.GuessRepository;
import com.guesswordgame.repository.UserRepository;
import com.guesswordgame.repository.WordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GameService {
    private static final int DAILY_LIMIT = 3;
    private static final int MAX_GUESSES = 5;

    private static final ZoneId GAME_ZONE = ZoneId.of("Asia/Kolkata");

    private final UserRepository userRepository;
    private final WordRepository wordRepository;
    private final GameRepository gameRepository;
    private final GuessRepository guessRepository;

    public GameService(UserRepository userRepository,
                       WordRepository wordRepository,
                       GameRepository gameRepository,
                       GuessRepository guessRepository) {
        this.userRepository = userRepository;
        this.wordRepository = wordRepository;
        this.gameRepository = gameRepository;
        this.guessRepository = guessRepository;
    }

    @Transactional
    public GameDtos.StatusResponse status(String username) {
        User user = getUser(username);
        LocalDate today = LocalDate.now(GAME_ZONE);
        int used = (int) gameRepository.countByUserAndGameDate(user, today);
        Long active = gameRepository.findFirstByUserAndGameDateAndCompletedFalseOrderByIdDesc(user, today)
                .map(Game::getId).orElse(null);
        return new GameDtos.StatusResponse(used, Math.max(0, DAILY_LIMIT - used), active);
    }

    @Transactional
    public GameDtos.StartGameResponse startGame(String username) {
        User user = getUser(username);
        LocalDate today = LocalDate.now(GAME_ZONE);

        var active = gameRepository.findFirstByUserAndGameDateAndCompletedFalseOrderByIdDesc(user, today);
        if (active.isPresent()) {
            int used = (int) gameRepository.countByUserAndGameDate(user, today);
            return new GameDtos.StartGameResponse(active.get().getId(), used, Math.max(0, DAILY_LIMIT - used), "RESUMED");
        }

        long used = gameRepository.countByUserAndGameDate(user, today);
        if (used >= DAILY_LIMIT) {
            throw new IllegalStateException("Daily limit reached. You can play at most 3 games per day.");
        }
        List<Word> words = wordRepository.findAll();
        if (words.isEmpty()) {
            throw new IllegalStateException("No words are configured by the admin.");
        }
        Word target = words.get(ThreadLocalRandom.current().nextInt(words.size()));
        Game game = gameRepository.save(new Game(user, target, today));
        int newUsed = (int) used + 1;
        return new GameDtos.StartGameResponse(game.getId(), newUsed, DAILY_LIMIT - newUsed, "STARTED");
    }

    @Transactional
    public GameDtos.GameResponse getGame(String username, Long gameId) {
        Game game = getOwnedGame(username, gameId);
        return toResponse(game, "");
    }

    @Transactional
    public GameDtos.GameResponse submitGuess(String username, Long gameId, String guess) {
        Game game = getOwnedGame(username, gameId);
        if (game.isCompleted()) {
            throw new IllegalStateException("This game has already ended.");
        }
        if (guess == null || !guess.matches("^[A-Z]{5}$")) {
            throw new IllegalArgumentException("Guess must be exactly 5 uppercase letters.");
        }
        if (game.getGuessesUsed() >= MAX_GUESSES) {
            throw new IllegalStateException("Maximum of 5 guesses reached.");
        }

        List<String> tiles = evaluate(guess, game.getWord().getValue());
        boolean won = guess.equals(game.getWord().getValue());
        game.setGuessesUsed(game.getGuessesUsed() + 1);
        game.setWon(won);
        String message = "";

        if (won) {
            game.setCompleted(true);
            game.setCompletedAt(LocalDateTime.now(GAME_ZONE));
            message = "CONGRATULATIONS! You found the word.";
        } else if (game.getGuessesUsed() == MAX_GUESSES) {
            game.setCompleted(true);
            game.setCompletedAt(LocalDateTime.now(GAME_ZONE));
            message = "BETTER LUCK NEXT TIME!";
        }

        guessRepository.save(new Guess(game, guess, String.join(",", tiles)));
        gameRepository.save(game);
        return toResponse(game, message);
    }

    private List<String> evaluate(String guess, String target) {
        String[] result = new String[5];
        int[] remaining = new int[26];

        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == target.charAt(i)) {
                result[i] = "GREEN";
            } else {
                remaining[target.charAt(i) - 'A']++;
            }
        }

        for (int i = 0; i < 5; i++) {
            if (result[i] != null) continue;
            int index = guess.charAt(i) - 'A';
            if (remaining[index] > 0) {
                result[i] = "ORANGE";
                remaining[index]--;
            } else {
                result[i] = "GREY";
            }
        }
        return List.of(result);
    }

    private GameDtos.GameResponse toResponse(Game game, String message) {
        List<Guess> guesses = guessRepository.findByGameOrderByIdAsc(game);
        List<GameDtos.GuessResultResponse> rows = new ArrayList<>();
        for (Guess guess : guesses) {
            rows.add(new GameDtos.GuessResultResponse(guess.getValue(), List.of(guess.getResult().split(","))));
        }
        return new GameDtos.GameResponse(
                game.getId(), game.getGuessesUsed(), MAX_GUESSES,
                game.isCompleted(), game.isWon(), message, rows);
    }

    private Game getOwnedGame(String username, Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found."));
        if (!game.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You do not have access to this game.");
        }
        return game;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }
}
