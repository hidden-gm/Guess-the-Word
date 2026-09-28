package com.guesswordgame.config;

import com.guesswordgame.entity.Role;
import com.guesswordgame.entity.User;
import com.guesswordgame.entity.Word;
import com.guesswordgame.repository.UserRepository;
import com.guesswordgame.repository.WordRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedData(UserRepository userRepository,
                               WordRepository wordRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (wordRepository.count() == 0) {
                wordRepository.saveAll(List.of(
                        "APPLE", "BRAIN", "CHAIR", "DREAM", "EARTH",
                        "FLAME", "GRAPE", "HOUSE", "IVORY", "JUICE",
                        "KNIFE", "LEMON", "MANGO", "NIGHT", "OCEAN",
                        "PIANO", "QUILT", "RIVER", "SMILE", "TIGER"
                ).stream().map(Word::new).toList());
            }

            if (!userRepository.existsByUsername("admin")) {
                userRepository.save(new User(
                        "admin",
                        passwordEncoder.encode("Admin1*"),
                        Role.ADMIN));
            }
        };
    }
}
