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



// TODO: add the logic to the SplashController
public class AddQuoteCtrl {

    private final ServerUtils server;

    private final MainCtrl mainCtrl;

//     @FXML
//     private TextField firstName;

//     @FXML
//     private TextField lastName;

//     @FXML
//     private TextField quote;

    @Inject
    public AddQuoteCtrl(final ServerUtils server, final MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    public void ok() {
        String nick = "jakub"; // TODO: get from TextField
        server.joinGame(nick);
        server.send("/app/game_join", nick);
    }
}
