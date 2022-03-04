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
import commons.Player;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class GameRepository {

    private static Set<Game> GAMES = new HashSet<>();

    public List<Game> getGames() {
        return GAMES.stream().toList();
    }

    public Game findById(UUID id) {
        Optional<Game> optional = GAMES.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst();

        if (optional.isEmpty()) return null;
        return optional.get();
    }

    public UUID addGame(Game game) {
        GAMES.add(game);
        return game.getId();
    }
    public void removeAllGames(){
        GAMES = new HashSet<>();;
    }
    public boolean removeGame(UUID id) {
        return GAMES.removeIf(g -> g.getId().equals(id));
    }

    public List<Player> getLeaderboard(UUID id) {
        Game game = this.findById(id);
        List<Player> players = game.getPlayers();
        return players.stream().sorted(Comparator.comparingInt(Player::getScore))
                .collect(Collectors.toList());
    }

}