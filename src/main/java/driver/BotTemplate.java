package driver;

import java.util.Arrays;
import java.util.HashMap;

public abstract class BotTemplate<T extends Comparable<T>> implements Player {
    private record MoveWithValue<T>(T value, int index) { }
    public record GameWithIndex(Game g, int index){}

    /**
     * A function to determine how good a board state is.
     * @param g game state
     * @param depthRemaining depth remaining in minimax
     * @param currentDepth current depth
     * @return a measure of how good the position is. Higher = better for RED
     */
    public abstract T utility(Game g, int depthRemaining, int currentDepth);

    /**
     * @param g The initial game state
     * @param depthRemaining the depth remaining in minimax search
     * @param currentDepth the current depth of minimax search
     * @param killerHeuristic the result of the "killer heuristic". -1 if there is no suggestion, It is recommended to put this child first (if it exists)
     * @return The children nodes to search, must be a nonempty/non-null list with all objects included have a non-null game.
     */
    public abstract Iterable<GameWithIndex> successors(Game g, int depthRemaining, int currentDepth, int killerHeuristic);

    /**
     * @param g game state
     * @return Max depth to search
     */
    public abstract int getMaxDepth(Game g);

    HashMap<Game, T> cache;
    int[] killerHeuristic;

    private int estimateCapacity(int bound){
        return 1 << Math.min(bound, 30);
    }


    @Override
    public int getMove(Game state){
        int depth = getMaxDepth(state);
        cache = new HashMap<>(estimateCapacity(depth));
        killerHeuristic = new int[depth + 1];
        Arrays.fill(killerHeuristic, -1);
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
            for (GameWithIndex successor: successors(g, maxDepth, currentDepth, killerHeuristic[maxDepth])) {
                MoveWithValue<T> bestNextMove = miniMax(successor.g, alpha, beta, maxDepth - 1, currentDepth + 1);
                if(bestMove == null || bestMove.value.compareTo(bestNextMove.value) < 0)
                    bestMove = new MoveWithValue<>(bestNextMove.value, successor.index);
                if(alpha == null || bestMove.value.compareTo(alpha) > 0)
                    alpha = bestMove.value;
                if(beta != null && bestMove.value.compareTo(beta) >= 0){
                    killerHeuristic[maxDepth] = successor.index;
                    break;
                }

            }
        }
        else {
            for (GameWithIndex successor: successors(g, maxDepth, currentDepth, killerHeuristic[maxDepth])) {
                MoveWithValue<T> bestNextMove = miniMax(successor.g, alpha, beta, maxDepth - 1, currentDepth + 1);
                if(bestMove == null || bestMove.value.compareTo(bestNextMove.value) > 0)
                    bestMove = new MoveWithValue<>(bestNextMove.value, successor.index);
                if(beta == null || bestMove.value.compareTo(beta) < 0)
                    beta= bestMove.value;
                if(alpha != null && bestMove.value.compareTo(alpha) <= 0) {
                    killerHeuristic[maxDepth] = successor.index;
                    break;
                }
            }
        }

        assert bestMove != null;
        cache.put(g, bestMove.value);
        return bestMove;
    }
}
