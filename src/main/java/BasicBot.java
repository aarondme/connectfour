import java.util.Arrays;
import java.util.LinkedList;

public class BasicBot extends BotTemplate<Integer>{
    final LinkedList<Integer> ints = new LinkedList<>(Arrays.asList(3, 2, 4, 1, 5, 0, 6));

    @Override
    Integer utility(Game g, int depthRemaining, int currentDepth) {
        GameResult result = g.getResult();
        if(result == GameResult.IN_PROGRESS || result == GameResult.DRAW)
            return 0;
        else if(result == GameResult.RED_WIN)
            return depthRemaining;
        else
            return -depthRemaining;
    }

    @Override
    LinkedList<Integer> defaultIterationOrder() {
        return ints;
    }

    @Override
    int getMaxDepth(Game ignored) {
        return 13;
    }
}
