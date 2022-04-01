/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.repository;


import commons.Game;
import commons.Leaderboard;
import commons.Player;
import commons.Question;

import org.springframework.stereotype.Repository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.List;

/**
 * Game repository that stores all multiplayer game objects.
 */
@Repository
public class GameRepository {

    private Set<Game> games;

    private UUID defaultID = UUID.randomUUID();

    public GameRepository() {
        games = new HashSet<>();
    }

    /**
     * Gets all games in the repository.
     * 
     * @return list of all games
     */
    public List<Game> getGames() {
        return games.stream().toList();
    }

    /**
     * Searches for a specific game with game id.
     * 
     * @param id id of the game
     * @return game object or null if specific game not found
     */
    public Game findById(final UUID id) {
        Optional<Game> optional = games.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst();

        if (optional.isEmpty()) {
            return null;
        }
        return optional.get();
    }

    /**
     * Creates and returns singleplayer game.
     *
     * @param questions list of questions
     * @return Game object
     */
    public Game getSingleGame(final List<Question> questions) {
        return new Game(defaultID, questions, false);
    }

    /**
     * Adds the passed game object into the games set.
     *
     * @param game the game object to add
     * @return id of game that was added
     */
    public UUID addGame(final Game game) {
        games.add(game);
        return game.getId();
    }

    /**
     * Removes all the games in the repo.
     */
    public void removeAllGames() {
        games = new HashSet<>();
    }

    /**
     * Remove specific game in game repo.
     *
     * @param id id of the game object
     * @return boolean whether game was successfully removed
     */
    public boolean removeGame(final UUID id) {
        return games.removeIf(g -> g.getId().equals(id));
    }

    /**
     * Creates and returns the leaderboard object based on
     * the Player list in the specified game.
     *
     * @param id id of the game object
     * @return leaderboard object
     */
    public Leaderboard getLeaderboard(final UUID id) {
        Game game = this.findById(id);
        return new Leaderboard(game.getPlayers());
    }

    /**
     * Updates the player's score on the server after every
     * question of the multiplayer game.
     *
     * @param game game object where player is in.
     * @param nick
     * @param score
     */
    public void addPlayerScore(final Game game, final String nick, final int score) {
        Player player = game.getPlayerByNick(nick);
        player.addScore(score);
    }
}
