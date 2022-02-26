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
package client;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import client.utils.DummyGame;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

//    private static final Injector INJECTOR = createInjector(new MyModule());
//    private static final MyFXML FXML = new MyFXML(INJECTOR);
    private static  String url = "http://localhost:8080/game/";
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
//        launch();

        //Initial POST request to get gameId
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/game/"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        String gameId = response.body().replaceAll("^\"|\"$", "");;
        System.out.println(gameId);
        url += gameId;

        //POST requests to add players to game
        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/lohithsai"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        request = HttpRequest.newBuilder()
                .uri(URI.create(url+"/Shibanu"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());




        //GET request to get list of players from game and to deserialize them
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder().GET().header("accept", "application/json").uri(URI.create(url)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        //parse JSON into objects
        ObjectMapper mapper = new ObjectMapper();
        DummyGame game = mapper.readValue((String) response.body(), DummyGame.class);
        System.out.println(game.getPlayers().get(0).getNickname());
        System.out.println(game.getPlayers().get(1).getNickname());
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

//        var overview = FXML.load(QuoteOverviewCtrl.class, "client", "scenes", "QuoteOverview.fxml");
//        var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");
//
//        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
//        mainCtrl.initialize(primaryStage, overview, add);
    }
}