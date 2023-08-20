package aaron;

import driver.BotTemplate;
import driver.Game;

public class AaronBot extends BotTemplate<Integer> {
    @Override
    public Integer utility(Game g, int depthRemaining, int currentDepth) {
        return null;
    }

    @Override
    public Iterable<BotTemplate.GameWithIndex> successors(Game g, int depthRemaining, int currentDepth, int killerHeuristic) {
        return null;
    }

    @Override
    public int getMaxDepth(Game g) {
        return 0;
    }
}
