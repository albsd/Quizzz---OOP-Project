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
package server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import server.repository.GameRepository;
import server.service.GameService;

import java.util.UUID;

public class GameControllerTest {

    private GameService service;

    private GameController ctrl;

    private UUID uuid;

    @BeforeEach
    public void setup() {
        service = new GameService(new GameRepository());
        ctrl = new GameController(service);
        uuid = ctrl.create();
    }

    @Test
    public void addNullGame() {
        var actual = ctrl.join(null, "johny");
        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void addNullNickName() {
        var actual = ctrl.join(uuid, null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void joinUninitializedGameWithValidNickname() {
        var actual = ctrl.join(UUID.randomUUID(), "nick");
        assertEquals(NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void joinInitializedGameWithValidNickname() {
        var actual = ctrl.join(uuid, "nick");
        assertEquals(OK, actual.getStatusCode());
    }
}