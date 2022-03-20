package server.repository;

import commons.LeaderboardMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SinglePlayerRepository extends JpaRepository<LeaderboardMessage, String> {

}
