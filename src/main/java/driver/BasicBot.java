package driver;

import java.util.ArrayList;

public class BasicBot extends BotTemplate<Integer>{
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
        int alternator = 1;
        int columnIndex = g.WIDTH / 2;
        for (int i = 0; i < g.WIDTH; i++) {
            columnIndex += i * alternator;
            alternator *= -1;

            if(columnIndex == killerHeuristic)
                continue;
            Game next = g.playMove(columnIndex);
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
