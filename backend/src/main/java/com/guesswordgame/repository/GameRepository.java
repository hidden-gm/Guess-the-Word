package com.guesswordgame.repository;

import com.guesswordgame.entity.Game;
import com.guesswordgame.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    long countByUserAndGameDate(User user, LocalDate date);
    long countByWord(com.guesswordgame.entity.Word word);
    Optional<Game> findFirstByUserAndGameDateAndCompletedFalseOrderByIdDesc(User user, LocalDate date);
    List<Game> findByUserOrderByGameDateDescIdDesc(User user);

    @Query("select count(distinct g.user.id) from Game g where g.gameDate = :date")
    long countDistinctUsersByDate(@Param("date") LocalDate date);

    @Query("select count(g) from Game g where g.gameDate = :date and g.won = true")
    long countWonGamesByDate(@Param("date") LocalDate date);

    @Query("select g.gameDate as gameDate, count(g) as tried, sum(case when g.won = true then 1 else 0 end) as correct " +
           "from Game g where g.user = :user group by g.gameDate order by g.gameDate desc")
    List<UserDailyProjection> getUserDailyReport(@Param("user") User user);

    interface UserDailyProjection {
        LocalDate getGameDate();
        long getTried();
        long getCorrect();
    }
}
