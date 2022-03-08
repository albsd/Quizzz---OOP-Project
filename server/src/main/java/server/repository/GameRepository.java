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
import server.FakeDatabase;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import java.util.Random;

@Repository
public class GameRepository {

    private Set<Game> games;
    private final FakeDatabase fd;
    private final List<Question> questions;

    public GameRepository() {
        games = new HashSet<>();
        fd = new FakeDatabase();
        questions = fd.getFakeQuestions();
    }

    public List<Game> getGames() {
        return games.stream().toList();
    }

    public Game findById(final UUID id) {
        Optional<Game> optional = games.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst();

        if (optional.isEmpty()) return null;
        return optional.get();
    }

    public UUID addGame(final Game game) {
        games.add(game);
        return game.getId();
    }
    public void removeAllGames() {
        games = new HashSet<>();
    }

    public boolean removeGame(final UUID id) {
        return games.removeIf(g -> g.getId().equals(id));
    }

    public Leaderboard getLeaderboard(final UUID id) {
        Game game = this.findById(id);
        List<Player> players = game.getPlayers();
        Leaderboard leaderboard = new Leaderboard();
        List<Player> rank = players.stream().
                sorted(Comparator.comparingInt(Player::getScore).reversed())
                .collect(Collectors.toList());
        leaderboard.setRanking(rank);
        return leaderboard;
    }

    public List<Question> getQuestions(final long seed) {
        Collections.shuffle(questions, new Random(seed));
        return questions;
    }

    public long generateSeed(final UUID id) {
        String str = id.toString();
        long seed = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            seed = seed + (long) ch;
        }
        return seed;
    }
}
