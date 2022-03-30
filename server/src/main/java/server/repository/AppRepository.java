package server.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * App repository that stores MAC address to nickname.
 */
@Repository
public class AppRepository {

    private Map<String, String> nicknames;

    public AppRepository() {
       this.nicknames = new HashMap<>();
    }

    //returns saved nick
    public String saveNickname(final String macAddress, final String nick) {
        return nicknames.put(macAddress, nick);
    }

    public String getNickname(final String macAddress) {
        return nicknames.get(macAddress);
    }
}
    