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
import commons.Leaderboard;
import commons.LobbyMessage;
import commons.Game;
import commons.Player;
import commons.PlayerUpdate;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Controller
public class ServerUtils {

    private final HttpClient client;

    private String kGameUrl;

    private String kAppUrl;

    private StompSession session;

    private Timer playerTimer;

    private TimerTask heartBeat;

    public ServerUtils() {
        this.client = HttpClient.newHttpClient();
        this.playerTimer = new Timer();
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
            this.kAppUrl = uri + "/app";
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
    public Player leaveLobby(final String nick) {
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
     * Calls the REST endpoint to leave the active game.
     *
     * @param nick  String of the user nickname
     * @param id    UUID of the game as a String
     * @return      Player that has left the game
     */
    public Player leaveGame(final String nick, final UUID id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/" + id + "/player/" + nick))
                .DELETE()
                .build();

        Player player = parseResponseToObject(request, new TypeReference<Player>() { });
        if (player != null) {
            send("/app/game/" + id  + "/leave", player);
        }
        return player;
    }

    /**
     * Calls the REST endpoint to get current lobby game object.
     *
     * @return List of players in a lobby
     */
    public Game getLobby() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/current"))
                .header("accept", "application/json")
                .GET()
                .build();

        return parseResponseToObject(request, new TypeReference<Game>() { });
    }

    /**
     * Fetch a leaderboard for a given Game with the id.
     * 
     * @param id UUID of the game
     * @return Leaderboard of players for the game
     */
    public Leaderboard getLeaderboard(final UUID id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/" + id + "/leaderboard"))
                .header("accept", "application/json")
                .GET()
                .build();

        return parseResponseToObject(request, new TypeReference<Leaderboard>() { });
    }

    /**
     * Fetch a leaderboard for a given Game with the id.
     *
     * @param id UUID of the game
     * @return Leaderboard of players for the game
     */
    public Game getGameById(final UUID id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/" + id))
                .header("accept", "application/json")
                .GET()
                .build();
        return parseResponseToObject(request, new TypeReference<Game>() { });
    }
    
    /**
     * Calls the REST endpoint to create and start a singleplayer game.
     *
     * @param nick  String of the user nickname
     * @return      Player that has started the game
     */
    public Game startSinglePlayer(final String nick) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/single/" + nick))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

       return parseResponseToObject(request, new TypeReference<Game>() { });
    }

    /**
     * Calls the REST endpoint to start the current lobby.
     * Nothing to return as all players already have instance
     * of Game object.
     */
    public void startMultiPlayer() {
        send("/app/lobby/chat",
                new LobbyMessage("Server", "", "Game is about to start. Have fun!"));
        send("/app/lobby/start", null);
    }

    /**
     * Updates player score every question.
     * @param id game object
     * @param nick name of user
     * @param score score of user
     */
    public void addScore(final UUID id, final String nick, final int score) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/" + id + "/score/" + nick))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Integer.toString(score)))
                .build();
        parseResponseToObject(request, new TypeReference<Game>() { });
    }

    public void sendGameResult(final String nick, final int score) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/leaderboard/" + nick + "/" + score))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        parseResponseToObject(request, new TypeReference<Leaderboard>() { });
    }

    public Leaderboard getSinglePlayerLeaderboard() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/leaderboard"))
                .header("accept", "application/json")
                .GET()
                .build();
        return parseResponseToObject(request, new TypeReference<Leaderboard>() { });
    }

    /**
     * Calls the REST endpoint to mark game as finished.
     *
     * @param id    UUID of the game as a String
     */
    public void removeGame(final UUID id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/" + id))
                .DELETE()
                .build();
        parseResponseToObject(request, new TypeReference<Game>() { });
    }

    /**
     * Request to update lobby player's heartbeat.
     *
     * @param nick name of player
     */
    public void updateLobbyPlayer(final String nick) {
        //catch in AppController
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kAppUrl + "/" + nick))
                .header("accept", "application/json")
                .GET()
                .build();
        parseResponseToObject(request, new TypeReference<Player>() { });
    }

    /**
     * Request to update game player's heartbeat.
     *
     * @param id id of game
     * @param nick name of player
     */
    public void updateGamePlayer(final UUID id, final String nick) {
        //catch in AppController
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kAppUrl + "/" + id + "/" + nick))
                .header("accept", "application/json")
                .GET()
                .build();
        parseResponseToObject(request, new TypeReference<Player>() { });
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

    public Timer getPlayerTimer() {
        return this.playerTimer;
    }

    public void startTimerTask(final TimerTask heartBeat) {
        this.heartBeat = heartBeat;
        //timer invokes currentTask (sending heartbeat to server) every 5 seconds
        playerTimer.scheduleAtFixedRate(heartBeat, 0, 5000);
    }

    public void stopTimerTask() {
        this.heartBeat.cancel();
    }
}
