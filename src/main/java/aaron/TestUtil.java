package aaron;

import driver.Game;
import driver.HumanPlayer;

public class TestUtil {
    public static void main(String[] args) {
        Game g = new Game();
        AaronBot a = new AaronBot();
        HumanPlayer h = new HumanPlayer();

        do {
            System.out.println(a.utility(g, 0, 14));
            g = g.playMove(h.getMove(g));
        } while (!g.isTerminal());


    }
}
