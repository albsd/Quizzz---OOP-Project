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

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Game;
import commons.Player;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
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

public class ServerUtils {

    private final String kBaseUrl;

    private final String kGameUrl;

    private final HttpClient client;
    private StompSession session = connect("ws://localhost:8080/websocket");

    public ServerUtils() {
        this.kBaseUrl = "http://localhost:8080";
        this.kGameUrl = kBaseUrl + "/game";

        this.client = HttpClient.newHttpClient();
    }

    // Initial POST request to get gameId
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

    public void joinGame(final String nick)
            throws IOException, InterruptedException {
        // POST requests to add players to game
        System.out.println(URI.create(kGameUrl + "/" + nick + "/join"));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/" + nick + "/join"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    private StompSession connect(final String url) {
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

    // GET request to get list of players from game and to deserialize them
    public List<Player> getPlayers() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kBaseUrl))
                .header("accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        // parse JSON into objects
        ObjectMapper mapper = new ObjectMapper();
        Game game = mapper.readValue((String) response.body(), Game.class);
        System.out.println(game.getPlayers().get(0).getNick());
        System.out.println(game.getPlayers().get(1).getNick());

        return game.getPlayers();
    }

    // public List<Quote> getQuotes() {
    // return ClientBuilder.newClient(new ClientConfig()) //
    // .target(SERVER).path("api/quotes") //
    // .request(APPLICATION_JSON) //
    // .accept(APPLICATION_JSON) //
    // .get(new GenericType<List<Quote>>() {});
    // }

    // public Quote addQuote(Quote quote) {
    // return ClientBuilder.newClient(new ClientConfig()) //
    // .target(SERVER).path("api/quotes") //
    // .request(APPLICATION_JSON) //
    // .accept(APPLICATION_JSON) //
    // .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
    // }
}
