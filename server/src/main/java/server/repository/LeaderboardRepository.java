package server.repository;

import commons.LeaderboardMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaderboardRepository extends JpaRepository<LeaderboardMessage, String> {

}
