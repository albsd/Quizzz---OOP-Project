package server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("")
public class AppController {

    /**
     * Endpoint to test whether the server is running.
     * Later on will be also used as an endpoint to check the players' heartbeat
     * 
     * @return true
     */
    @GetMapping(path = { "", "/" })
    public boolean identity() {
        return true;
    }

}
