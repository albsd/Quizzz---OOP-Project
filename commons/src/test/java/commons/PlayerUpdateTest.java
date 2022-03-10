package commons;

        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;

        import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerUpdateTest {

    private String nick1;
    private String nick2;
    private PlayerUpdate.Type type1;
    private PlayerUpdate.Type type2;
    private PlayerUpdate update1;
    private PlayerUpdate update2;


    @BeforeEach
    void setup() {
        nick1 = "Adam";
        nick2 = "Kevin";
        type1 = PlayerUpdate.Type.join;
        type2 = PlayerUpdate.Type.leave;
        update1 = new PlayerUpdate(nick1, type1);
        update2 = new PlayerUpdate(nick2, type2);
    }

    @Test
    void testGetters() {
        assertEquals(nick1, update1.getNick());
        assertEquals(type1, update1.getContent());

        assertEquals(nick2, update2.getNick());
        assertEquals(type2, update2.getContent());
    }
}
