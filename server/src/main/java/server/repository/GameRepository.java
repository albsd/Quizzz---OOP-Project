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
import commons.ScoreMessage;
import org.springframework.stereotype.Repository;
import server.FakeDatabase;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.List;
@Repository
public class GameRepository {

    private Set<Game> games;
    private final FakeDatabase fd;

    public GameRepository() {
        games = new HashSet<>();
        fd = new FakeDatabase();
    }

    public List<Game> getGames() {
        return games.stream().toList();
    }

    public Game findById(final UUID id) {
        Optional<Game> optional = games.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst();

        if (optional.isEmpty()) {
            return null;
        }
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

    public void updatePlayerScore(final ScoreMessage sm) {
        Game game = findById(sm.getId());
        Player player = game.getPlayerByNick(sm.getNick());
        int score;
        if (sm.getType().equals("multiple")) {
            score = calculateMulChoicePoints(sm.getContent());
        } else {
            score = calculateOpenPoints(sm.getAnswer(), sm.getOption(), sm.getContent());
        }
        player.setScore(score);
    }

    private int calculateMulChoicePoints(final int time) {
        int base = 50;
        int bonusScore = calculateBonusPoints(time);
        return base + bonusScore;
    }

    private int calculateOpenPoints(final long answer, final long option, final int time) {
        int bonusScore = calculateBonusPoints(time);
        int offPercentage = (int) Math.round(((double) Math.abs((option - answer)) / answer) * 100);
        int accuracyPercentage = 100 - offPercentage;
        if (accuracyPercentage < 0) {
            accuracyPercentage = 0;
        }
        int base = (accuracyPercentage / 10) * 10;
        return base + bonusScore;
    }

    private int calculateBonusPoints(final int time) {
        return (time / 1000) * 2;
    }
}
