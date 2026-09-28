package com.guesswordgame.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "guesses")
@Getter
@Setter
@NoArgsConstructor
public class Guess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Game game;

    @Column(name = "guess_value", nullable = false, length = 5)
    private String value;

    @Column(nullable = false, length = 40)
    private String result;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Guess(Game game, String value, String result) {
        this.game = game;
        this.value = value;
        this.result = result;
        this.createdAt = LocalDateTime.now();
    }
}
