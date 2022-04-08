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
import commons.Activity;
import commons.LobbyMessage;
import commons.Image;
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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Controller
public class ServerUtils {

    private final HttpClient client;

    private String kGameUrl;
    private String kActivityUrl;
    private String kAppUrl;

    private StompSession session;

    private Timer heartBeatTimer;

    private TimerTask heartBeat;

    private String macAddress;

    public ServerUtils() {
        this.client = HttpClient.newHttpClient();
        this.heartBeatTimer = new Timer();
        this.macAddress = initMacAddress();
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
            this.kActivityUrl = uri + "/activity";
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

    // REQUESTS FOR THE LOBBY =========================================================================================
    /**
     * Calls the REST endpoint to get current lobby game object.
     *
     * @return List of players in a lobby
     */
    public Game getLobby() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/lobby"))
                .header("accept", "application/json")
                .GET()
                .build();

        return parseResponseToObject(request, new TypeReference<Game>() { });
    }
    
    /**
     * Calls the REST endpoint to join the current active lobby.
     *
     * @param nick  String of the user nickname
     * @return      Player that has joined the game
     */
    public Player joinLobby(final String nick) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/join/" + nick))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        return parseResponseToObject(request, new TypeReference<Player>() { });
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

        return parseResponseToObject(request, new TypeReference<Player>() { });
    }

    /**
     * Request to update lobby player's heartbeat.
     *
     * @param nick name of player
     */
    public void updateLobbyPlayer(final String nick) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kAppUrl + "/" + nick))
                .header("accept", "application/json")
                .GET()
                .build();

        parseResponseToObject(request, new TypeReference<Player>() { });
    }
    
    /**
     * Calls the REST endpoint to start the current lobby.
     * Nothing to return as all players already have instance
     * of Game object.
     */
    public void startMultiPlayer() {
        send("/app/lobby/chat", new LobbyMessage("Server", "", "Game is about to start. Have fun!"));
        send("/app/lobby/start", null);
    }

    // REQUESTS FOR A GAME WITH ID ====================================================================================
    /**
     * Fetch a game with the given UUID.
     *
     * @param id UUID of the game
     * @return Game object
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

        return parseResponseToObject(request, new TypeReference<Player>() { });
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
     * Calls the REST endpoint to mark game as finished.
     *
     * @param id    UUID of the game as a String
     */
    public void setGameOver(final UUID id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/" + id))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        parseResponseToObject(request, new TypeReference<Game>() { });
    }
    
    // REQUESTS FOR A SINGLEPLAYER GAME ===============================================================================
    /**
     * Calls the REST endpoint to create a singleplayer game.
     *
     * @param nick  Nickname of player
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
     * Fetch an all-time leaderboard for singleplayer.
     * 
     * @return Leaderboard of all players
     */
    public Leaderboard getSinglePlayerLeaderboard() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/leaderboard"))
                .header("accept", "application/json")
                .GET()
                .build();

        return parseResponseToObject(request, new TypeReference<Leaderboard>() { });
    }
    
    /**
     * Store the player's score for a single-player game.
     * 
     * @param nick Name of tha player
     * @param score Player's score
     */
    public void sendGameResult(final String nick, final int score) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/leaderboard/" + nick + "/" + score))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        parseResponseToObject(request, new TypeReference<Leaderboard>() { });
    }

    // COMMON REQUESTS TO UPDATE PLAYER'S STATUS =======================================================================
    /**
     * Updates player score every question.
     * 
     * @param id    UUID of the given game
     * @param nick  Nickname of the user
     * @param score score of the user
     */
    public void addScore(final UUID id, final String nick, final int score) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kGameUrl + "/" + id + "/score/" + nick))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Integer.toString(score)))
                .build();

        parseResponseToObject(request, new TypeReference<Game>() { });
    }

    /**
     * Updates the player's status of the current question to finished.
     * 
     * @param id    UUID of the given game
     * @param nick  Nickname of the user
     */
    public void updatePlayerFinished(final UUID id, final String nick) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kAppUrl + "/" + id + "/finishedtimer/" + nick))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        parseResponseToObject(request, new TypeReference<Game>() { });
    }


    // REQUESTS FOR THE DATABASE OF ACTIVITIES  =======================================================================
    public List<Activity> getAllActivities() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kActivityUrl))
                .header("accept", "application/json")
                .GET()
                .build();

        return parseResponseToObject(request, new TypeReference<List<Activity>>() { });
    }

    public Activity addActivity(final Activity activity) {
        ObjectMapper mapper = new ObjectMapper();
        String activityString = "";
        try {
            activityString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kActivityUrl))
                .headers("accept", "application/json", "content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(activityString))
                .build();

        return parseResponseToObject(request, new TypeReference<Activity>() { });
    }

    public Image sendImage(final Image image) {
        ObjectMapper mapper = new ObjectMapper();
        String imageString = "";
        try {
            imageString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
                    HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(kActivityUrl + "/img"))
                    .headers("accept", "application/json", "content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(imageString))
                    .build();

        return parseResponseToObject(request, new TypeReference<Image>() { });
    }

    public Image getImage(final String path) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kActivityUrl + "/img?path=" + path))
                .headers("accept", "application/json")
                .GET()
                .build();

        return parseResponseToObject(request, new TypeReference<Image>() { });
    }

    public Activity deleteActivity(final Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kActivityUrl + "/" + id))
                .DELETE()
                .build();

        return parseResponseToObject(request, new TypeReference<Activity>() { });
    }

    public List<Activity> addActivities(final List<Activity> activities) {
        ObjectMapper mapper = new ObjectMapper();
        String activitiesString = "";
        try {
            activitiesString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(activities);

        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kActivityUrl + "/activities"))
                .headers("accept", "application/json", "content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(activitiesString))
                .build();
        return parseResponseToObject(request, new TypeReference<List<Activity>>() { });
    }

    public List<Image> sendImages(final List<Image> images) {
        ObjectMapper mapper = new ObjectMapper();
        String imagesString = "";
        try {
            imagesString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(images);

        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kActivityUrl + "/imgs"))
                .headers("accept", "application/json", "content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(imagesString))
                .build();
        return parseResponseToObject(request, new TypeReference<List<Image>>() { });
    }

    // REQUESTS FOR SERVER SAVED NICKNAME  ============================================================================
    public String getNickname() {
        if (macAddress == null) {
            return null;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kAppUrl + "/nick/" + macAddress))
                .headers("accept", "application/json")
                .GET()
                .build();
        Player player = parseResponseToObject(request, new TypeReference<Player>() { });
        if (player == null) {
            return null;
        }
        return player.getNick();
    }

    public void saveNickname(final String nick) {
        if (macAddress == null) {
            return;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kAppUrl + "/nick/" + macAddress + "/" + nick))
                .headers("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(""))
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

    /**
     * Starts the Timer task of transmiting the time to server.
     * The timer invokes sending heartbeat to the server every 5 seconds
     * @param heartBeat timer task to send heart beat to server.
     */
    public void startHeartbeat(final TimerTask heartBeat) {
        this.heartBeat = heartBeat;
        heartBeatTimer.scheduleAtFixedRate(heartBeat, 0, 5000);
    }

    /**
     * Stops sending the heartbeat of player.
     */
    public void cancelHeartbeat() {
        heartBeat.cancel();
        heartBeatTimer.purge();
    }

    private String initMacAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = ni.getHardwareAddress();

            String[] hexadecimal = new String[hardwareAddress.length];
            for (int i = 0; i < hardwareAddress.length; i++) {
                hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
            }
            return String.join("_", hexadecimal);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
