package driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class BasicBot extends BotTemplate<Integer>{
    final LinkedList<Integer> ints = new LinkedList<>(Arrays.asList(3, 2, 4, 1, 5, 0, 6));

    @Override
    public Integer utility(Game g, int depthRemaining, int currentDepth) {
        GameResult result = g.getResult();
        if(result == GameResult.IN_PROGRESS || result == GameResult.DRAW)
            return 0;
        else if(result == GameResult.RED_WIN)
            return depthRemaining;
        else
            return -depthRemaining;
    }

    @Override
    public Iterable<Game> successors(Game g, int depthRemaining, int currentDepth, int killerHeuristic) {
        ArrayList<Game> out = new ArrayList<>();
        if(killerHeuristic != -1){
            Game next = g.playMove(killerHeuristic);
            if(next != null)
                out.add(next);
        }
        for (int i: ints) {
            if(i == killerHeuristic)
                continue;
            Game next = g.playMove(i);
            if(next != null)
                out.add(next);
        }
        return out;
    }

    @Override
    public int getMaxDepth(Game ignored) {
        return 17;
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
