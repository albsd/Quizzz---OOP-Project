package server.repository;

import commons.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaderboardRepository extends JpaRepository<GameResult, String> {

}
