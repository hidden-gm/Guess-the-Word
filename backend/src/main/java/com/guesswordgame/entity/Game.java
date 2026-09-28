package com.guesswordgame.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "games", indexes = {
        @Index(name = "idx_games_user_date", columnList = "user_id, game_date")
})
@Getter
@Setter
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Word word;

    @Column(nullable = false)
    private LocalDate gameDate;

    @Column(nullable = false)
    private int guessesUsed;

    @Column(nullable = false)
    private boolean won;

    @Column(nullable = false)
    private boolean completed;

    private LocalDateTime completedAt;

    public Game(User user, Word word, LocalDate date) {
        this.user = user;
        this.word = word;
        this.gameDate = date;
        this.guessesUsed = 0;
        this.won = false;
        this.completed = false;
    }
}
