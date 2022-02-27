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

import client.scenes.SplashController;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import static com.google.inject.Guice.createInjector;


import java.io.IOException;
import java.net.URISyntaxException;

import static com.google.inject.Guice.createInjector;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
//        Parent root = FXMLLoader.load(Main.class.getResource("Splash.fxml"));
        var root  = FXML.load(SplashController.class, "client", "scenes", "Splash.fxml");
        Scene scene = new Scene(root.getValue());
        Image logo = new Image(Main.class.getResourceAsStream("/icon.png"));
        stage.getIcons().add(logo);
        stage.setTitle("Energy Quizzz");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

}