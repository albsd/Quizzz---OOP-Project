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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import commons.Game;
import commons.Player;

public class ServerUtils {

    private final URI kBaseUrl;

    private final URI kGameUrl;

    private final HttpClient client;

    public ServerUtils() {
        this.kBaseUrl = URI.create("http://localhost:8080");
        this.kGameUrl = kBaseUrl.resolve("./game");

        this.client = HttpClient.newHttpClient();
    }

    // Initial POST request to get gameId
    public String createGame() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(kGameUrl)
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        String gameId = response.body().replaceAll("^\"|\"$", "");

        System.out.println(gameId);
        return gameId;
    }

    public void joinGame() throws IOException, InterruptedException {
        // POST requests to add players to game
        HttpRequest request = HttpRequest.newBuilder()
                .uri(kGameUrl.resolve("./lohithsai"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    // GET request to get list of players from game and to deserialize them
    public List<Player> getPlayers() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(kBaseUrl)
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