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
import org.springframework.stereotype.Repository;
import server.service.ServerQuestionTimer;

import java.util.*;

@Repository
public class GameRepository {

    private static Set<Game> games = new HashSet<>();

    private static Set<ServerQuestionTimer> timers = new HashSet<>();

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

    public ServerQuestionTimer findTimerById(final UUID id) {
        Optional<ServerQuestionTimer> optional = timers.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();

        if (optional.isEmpty()) return null;
        return optional.get();
    }

    public UUID addTimer(final ServerQuestionTimer timer) {
        timers.add(timer);
        return timer.getId();
    }

    public void removeAllGames() {
        games = new HashSet<>();
    }
    public boolean removeGame(final UUID id) {
        return games.removeIf(g -> g.getId().equals(id));
    }

}
