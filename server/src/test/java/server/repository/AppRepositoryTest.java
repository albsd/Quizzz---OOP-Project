package server.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppRepositoryTest {

    private String mac1;
    private String mac2;
    private String nick1;
    private String nick2;
    private AppRepository appRepo;
    private Map<String, String> nicknames;

    @BeforeEach
    public void setup() {
        nick1 = "Charlie";
        nick2 = "Speedy";
        mac1 = "E2_43_F2_J6_O9_3F";
        mac2 = "A8_43_G2_J6_O9_0F";
        nicknames = new HashMap<>();
        nicknames.put(mac1, nick1);
        nicknames.put(mac2, nick2);
        appRepo = new AppRepository();
        appRepo.saveNickname(mac1, nick1);
        appRepo.saveNickname(mac2, nick2);
    }

    @Test
    void getNickMapping() {
        assertEquals(nicknames, appRepo.getNickMapping());
    }

    @Test
    void getNickname() {
        assertEquals(nick1, appRepo.getNickname(mac1));
        assertEquals(nick2, appRepo.getNickname(mac2));
    }
}
