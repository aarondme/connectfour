import java.util.HashMap;

public abstract class BotTemplate<T extends Comparable<T>> implements Player {
    private record MoveWithValue<T>(T value, int index) { }

    /**
     * A function to determine how good a board state is.
     * @param g game state
     * @param depthRemaining depth remaining in minimax
     * @param currentDepth current depth
     * @return a measure of how good the position is. Higher = better for RED
     */
    abstract T utility(Game g, int depthRemaining, int currentDepth);

    /**
     * @param g game state
     * @return A permutation of column indices; the order in which the search will check. Must include all columns
     */
    abstract int[] iterationOrder(Game g);

    /**
     * @param g game state
     * @return Max depth to search
     */
    abstract int getMaxDepth(Game g);

    HashMap<Game, T> cache;

    private int estimateCapacity(int bound){
        return 1 << (bound * 7 / 8);
    }

    @Override
    public int getMove(Game state){
        int depth = getMaxDepth(state);
        cache = new HashMap<>(estimateCapacity(depth));
        MoveWithValue<T> bestMove = miniMax(state, null, null, depth, 0);
        return bestMove.index;
    }

    MoveWithValue<T> miniMax(Game g, T alpha,T beta, int maxDepth, int currentDepth){
        if(cache.containsKey(g)){
            return new MoveWithValue<>(cache.get(g), -1);
        }
        if(maxDepth == 0 || g.isTerminal()){
            T util = utility(g, maxDepth, currentDepth);
            cache.put(g, util);
            return new MoveWithValue<>(util, -1);
        }

        MoveWithValue<T> bestMove = null;
        if(g.isFirstPlayersMove()){
            for (int index: iterationOrder(g)) {
                Game successor = g.playMove(index);
                if(successor == null)
                    continue;
                MoveWithValue<T> bestNextMove = miniMax(successor, alpha, beta, maxDepth - 1, currentDepth + 1);
                if(bestMove == null || bestMove.value.compareTo(bestNextMove.value) < 0)
                    bestMove = new MoveWithValue<>(bestNextMove.value, index);
                if(alpha == null || bestMove.value.compareTo(alpha) > 0)
                    alpha = bestMove.value;
                if(beta != null && bestMove.value.compareTo(beta) >= 0)
                    break;
            }
        }
        else {
            for (int index: iterationOrder(g)) {
                Game successor = g.playMove(index);
                if(successor == null)
                    continue;
                MoveWithValue<T> bestNextMove = miniMax(successor, alpha, beta, maxDepth - 1, currentDepth + 1);
                if(bestMove == null || bestMove.value.compareTo(bestNextMove.value) > 0)
                    bestMove = new MoveWithValue<>(bestNextMove.value, index);
                if(beta == null || bestMove.value.compareTo(beta) < 0)
                    beta= bestMove.value;
                if(alpha != null && bestMove.value.compareTo(alpha) <= 0)
                    break;
            }
        }

        cache.put(g, bestMove.value);
        return bestMove;
    }
}
