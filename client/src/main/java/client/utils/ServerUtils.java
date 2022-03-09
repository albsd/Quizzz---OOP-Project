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
import commons.Game;
import commons.Leaderboard;
import commons.Player;
import commons.Question;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
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
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Controller
public class ServerUtils {

    private final String kGameUrl;

    private final HttpClient client;

    private static StompSession session = connect("ws://localhost:8080/websocket");

    public ServerUtils() {
        this.kGameUrl = "http://localhost:8080/game";
        this.client = HttpClient.newHttpClient();
    }

    // Initial POST request to get gameId
    @Deprecated
    public String createGame() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        String gameId = response.body().replaceAll("^\"|\"$", "");

        System.out.println(gameId);
        return gameId;
    }

    private static StompSession connect(final String url) {
        var wsClient = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(wsClient);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            return stomp.connect(url, new StompSessionHandlerAdapter() {
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException();
        }
        throw new IllegalStateException();
    }

    public <T> void registerForMessages(final String dest,
            final Class<T> type,
            final Consumer<T> consumer) {
        session.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(final StompHeaders headers) {
                return type;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(final StompHeaders headers,
                    final Object payload) {
                consumer.accept((T) payload);
            }
        });
    }

    public void send(final String dest, final Object o) {
        session.send(dest, o);
    }

    /**
     * Calls the REST endpoint to join the current active lobby.
     *
     * @param nick String of the user nickname
     * @return Player that has joined the game
     */
    public Player joinGame(final String nick) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/join/" + nick))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        return parseResponseToObject(request, new TypeReference<>() {
        });
    }

    /**
     * Calls the REST endpoint to leave the current active lobby.
     *
     * @param nick String of the user nickname
     * @return Player that has left the game
     */
    public Player leaveGame(final String nick) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/leave/" + nick))
                .DELETE()
                .build();

        return parseResponseToObject(request, new TypeReference<>() {
        });
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
        Game game = parseResponseToObject(request, new TypeReference<>() {
        });
        if (game == null) return null;
        return game.getPlayers();
    }

    public Leaderboard getLeaderboard(final String id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/" + id + "/leaderboard"))
                .header("accept", "application/json")
                .GET()
                .build();

        return parseResponseToObject(request, new TypeReference<>() {
        });
    }

    public List<Question> getQuestions(final String id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/"
                        + id + "/question"))
                .header("accept", "application/json")
                .GET()
                .build();

        return parseResponseToObject(request, new TypeReference<>() {
        });
    }

    /**
     * Utility method to send and receive a Player object.
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

            if (response.statusCode() != 200) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            T obj = mapper.readValue(response.body(), type);
            return obj;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
