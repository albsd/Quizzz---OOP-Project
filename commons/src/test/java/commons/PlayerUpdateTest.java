package commons;

        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;

        import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerUpdateTest {

    private Player p1;
    private Player p2;
    private PlayerUpdate.Action action1;
    private PlayerUpdate.Action action2;
    private PlayerUpdate update1;
    private PlayerUpdate update2;


    @BeforeEach
    void setup() {
        p1 = new Player("Adam");
        p2 = new Player("Kevin");
        action1 = PlayerUpdate.Action.join;
        action2 = PlayerUpdate.Action.leave;
        update1 = new PlayerUpdate(p1, action1);
        update2 = new PlayerUpdate(p2, action2);
    }

    @Test
    void testGetters() {
        assertEquals(p1, update1.getPlayer());
        assertEquals(action1, update1.getContent());

        assertEquals(p2, update2.getPlayer());
        assertEquals(action2, update2.getContent());
    }
}
