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
package client.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Player;
import commons.PlayerUpdate;
import commons.Game;
import commons.Leaderboard;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Controller
public class ServerUtils {

    private final HttpClient client;

    private String kGameUrl;

    private StompSession session;

    public ServerUtils() {
        this.client = HttpClient.newHttpClient();
    }

    public String isRunning(final String host, final String port) {
        try {
            final URI uri = URI.create("http://" + host + ":" + port);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            // if the above code does not throw -> we can set the urls
            this.kGameUrl = uri + "/game";
            this.session = connect("ws://" + host + ":" + port + "/websocket");
            return null;
        } catch (IllegalArgumentException e) {
            return "Host is invalid.\n"
                    + "Supported are IPv4 ('192.168.xxx.xxx') or ('localhost')";
        } catch (Exception e) {
            return "The server on the given address is not running.\n"
                    + "Make sure the server is running first. To get more help refer to README.md";
        }
    }

    private static StompSession connect(final String url) {
        var wsClient = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(wsClient);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            return stomp.connect(url, new StompSessionHandlerAdapter() { }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException();
        }
        throw new IllegalStateException();
    }

    public <T> Subscription registerForMessages(final String dest, final Class<T> type, final Consumer<T> consumer) {
        return session.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(final StompHeaders headers) {
                return type;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(final StompHeaders headers, final Object payload) {
                try {
                    consumer.accept((T) payload);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void send(final String dest, final Object o) {
        session.send(dest, o);
    }

    /**
     * Calls the REST endpoint to join the current active lobby.
     *
     * @param nick  String of the user nickname
     * @return      Player that has joined the game
     */
    public Player joinGame(final String nick) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/join/" + nick))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        Player player = parseResponseToObject(request, new TypeReference<Player>() { });
        if (player != null) {
            send("/app/update/player", new PlayerUpdate(player.getNick(), PlayerUpdate.Type.join));
        }
        return player;
    }

    /**
     * Calls the REST endpoint to leave the current active lobby.
     *
     * @param nick  String of the user nickname
     * @return      Player that has left the game
     */
    public Player leaveGame(final String nick) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/leave/" + nick))
                .DELETE()
                .build();

        Player player = parseResponseToObject(request, new TypeReference<Player>() { });
        if (player != null) {
            send("/app/update/player", new PlayerUpdate(player.getNick(), PlayerUpdate.Type.leave));
        }
        return player;
    }

    /**
     * Calls the REST endpoint to get list of all players in the lobby.
     *
     * @return List of players in a lobby
     */
    public List<Player> getPlayers() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/current"))
                .header("accept", "application/json")
                .GET()
                .build();

        Game game = parseResponseToObject(request, new TypeReference<Game>() { });
        if (game == null) return null;
        return game.getPlayers();
    }

    public Leaderboard getLeaderboard(final String id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/" + id + "/leaderboard"))
                .header("accept", "application/json")
                .GET()
                .build();

        return parseResponseToObject(request, new TypeReference<Leaderboard>() { });
    }

    /**
     * Calls the REST endpoint to start the current lobby.
     *
     * @return The game that has just started
     */
    public Game startGame() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/start"))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        Game game = parseResponseToObject(request, new TypeReference<Game>() { });
        if (game != null) {
            send("/app/lobby/start", game);
        }
        return game;
    }

    /**
     * Updates player score on server side every 10 questions for leaderboard.
     * @param id game id to find game
     * @param nick name of player
     * @param score score of player
     * @return The game that was used to update with
     */
    public Game updateScore(final UUID id, final String nick, final String score) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/" + id + "/score/" + nick))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(score))
                .build();
        Game game = parseResponseToObject(request, new TypeReference<Game>() { });
        return game;
    }

    /**
     * Utility method to parse HttpResponse to a given object type.
     *
     * @param <T>     Type the response shall get parsed to
     * @param request Request to be sent
     * @param type    Expected type of the response
     * @return Parsed response as the given instance of class `type`
     */
    private <T> T parseResponseToObject(final HttpRequest request, final TypeReference<T> type) {
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) return null;

            ObjectMapper mapper = new ObjectMapper();
            T obj = mapper.readValue(response.body(), type);
            return obj;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
