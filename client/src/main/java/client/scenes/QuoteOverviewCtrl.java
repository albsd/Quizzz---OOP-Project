// /*
//  * Copyright 2021 Delft University of Technology
//  *
//  * Licensed under the Apache License, Version 2.0 (the "License");
//  * you may not use this file except in compliance with the License.
//  * You may obtain a copy of the License at
//  *
//  *    http://www.apache.org/licenses/LICENSE-2.0
//  *
//  * Unless required by applicable law or agreed to in writing, software
//  * distributed under the License is distributed on an "AS IS" BASIS,
//  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  * See the License for the specific language governing permissions and
//  * limitations under the License.
//  */
package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Quote;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import client.utils.ServerUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

// TODO:
// Rename to LobbyCtrl
public class QuoteOverviewCtrl implements Initializable {

    private final ServerUtils server;

    private final MainCtrl mainCtrl;

    private final List<Player> players;

    @Inject
    public QuoteOverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.players = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        server.registerForMessages("/topic/game_join", Player.class, p -> {
            players.add(p);
        });
    }
}