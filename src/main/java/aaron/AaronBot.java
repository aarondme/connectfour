package aaron;

import driver.BotTemplate;
import driver.Game;
import driver.GameResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class AaronBot extends BotTemplate<Integer> {
    final LinkedList<Integer> ints = new LinkedList<>(Arrays.asList(3, 2, 4, 1, 5, 0, 6));
    @Override
    public Integer utility(Game g, int depthRemaining, int currentDepth) {
        GameResult result = g.getResult();

        if(result == GameResult.RED_WIN)
            return Integer.MAX_VALUE - currentDepth;
        else if (result == GameResult.YELLOW_WIN)
            return Integer.MIN_VALUE + currentDepth;
        return 0;
    }

    @Override
    public Iterable<Game> successors(Game g, int depthRemaining, int currentDepth, int killerHeuristic) {
        Game next;
        ArrayList<Game> out = new ArrayList<>();
        if(killerHeuristic >= 0){
            next = g.playMove(killerHeuristic);
            if(next != null)
                out.add(next);
        }
        for (int i: ints) {
            if(i == killerHeuristic)
                continue;
            next = g.playMove(i);
            if(next != null)
                out.add(next);
        }
        return out;
    }

    @Override
    public int getMaxDepth(Game g) {
        return 16;
    }

    @Override
    public Integer negativeInfinity() {
        return Integer.MIN_VALUE;
    }

    @Override
    public Integer positiveInfinity() {
        return Integer.MAX_VALUE;
    }
}
