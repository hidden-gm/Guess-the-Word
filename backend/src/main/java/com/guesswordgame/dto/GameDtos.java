package com.guesswordgame.dto;

import java.util.List;

public final class GameDtos {
    private GameDtos() {}

    public record StartGameResponse(Long gameId, int gamesUsedToday, int gamesRemainingToday, String status) {}
    public record GuessRequest(String guess) {}
    public record GuessResultResponse(String guess, List<String> tiles) {}
    public record GameResponse(Long gameId, int guessCount, int maxGuesses, boolean completed, boolean won,
                                String message, List<GuessResultResponse> guesses) {}
    public record DailyReport(long users, long correctGuesses) {}
    public record UserDailyReport(String date, long wordsTried, long correctGuesses) {}
    public record WordResponse(Long id, String value) {}
    public record WordRequest(String value) {}
    public record StatusResponse(int gamesUsedToday, int gamesRemainingToday, Long activeGameId) {}
}
