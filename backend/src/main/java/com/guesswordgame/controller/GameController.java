package com.guesswordgame.controller;

import com.guesswordgame.dto.GameDtos;
import com.guesswordgame.service.GameService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/status")
    public GameDtos.StatusResponse status(Authentication authentication) {
        return gameService.status(authentication.getName());
    }

    @PostMapping("/start")
    public GameDtos.StartGameResponse start(Authentication authentication) {
        return gameService.startGame(authentication.getName());
    }

    @GetMapping("/{gameId}")
    public GameDtos.GameResponse get(@PathVariable Long gameId, Authentication authentication) {
        return gameService.getGame(authentication.getName(), gameId);
    }

    @PostMapping("/{gameId}/guess")
    public GameDtos.GameResponse guess(@PathVariable Long gameId,
                                       @RequestBody GameDtos.GuessRequest request,
                                       Authentication authentication) {
        return gameService.submitGuess(authentication.getName(), gameId, request.guess());
    }
}
