package server.repository;

import commons.Activity;
import commons.SinglePlayerLeaderboardMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SinglePlayerRepository extends JpaRepository<SinglePlayerLeaderboardMessage, String> {

}