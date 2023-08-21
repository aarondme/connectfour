package driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class BotTemplate<T extends Comparable<T>> implements Player {
    public record MinimaxResult<T>(Game successor, int depth, T initialAlpha, T initialBeta){}
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
    public abstract Iterable<Game> successors(Game g, int depthRemaining, int currentDepth, int killerHeuristic);

    /**
     * @param g game state
     * @return Max depth to search
     */
    public abstract int getMaxDepth(Game g);

    public abstract T negativeInfinity();

    public abstract T positiveInfinity();

    HashMap<Game, T> cache;
    int[] killerHeuristic;
    ArrayList<MinimaxResult<T>> results;


    private int estimateCapacity(int bound){
        return 1 << Math.min(bound, 30);
    }


    @Override
    public int getMove(Game state){
        int depth = getMaxDepth(state);
        cache = new HashMap<>(estimateCapacity(depth));
        killerHeuristic = new int[depth + 1];
        Arrays.fill(killerHeuristic, -1);
        results = new ArrayList<>();
        miniMax(state, negativeInfinity(), positiveInfinity(), depth, 0);
        MinimaxResult<T> r = results.get(0);
        return getIndex(state, r.successor);
    }

    private int getIndex(Game start, Game child){
        for (int i = 0; i < start.WIDTH; i++) {
            Game n = start.playMove(i);
            if(n != null && n.equals(child))
                return i;
        }
        return -1;
    }

    T miniMax(Game g, T alpha,T beta, int maxDepth, int currentDepth){
        if(cache.containsKey(g)){
            return cache.get(g);
        }
        if(maxDepth == 0 || g.isTerminal()){
            T util = utility(g, maxDepth, currentDepth);
            cache.put(g, util);
            return util;
        }

        T value;
        Game bestNextMove = null;
        T initialAlpha = alpha;
        T initialBeta = beta;
        if(g.isFirstPlayersMove()){
            value = negativeInfinity();
            for (Game successor: successors(g, maxDepth, currentDepth, killerHeuristic[maxDepth])) {
                T nextValue = miniMax(successor, alpha, beta, maxDepth - 1, currentDepth + 1);
                if(value.compareTo(nextValue) < 0){
                    bestNextMove = successor;
                    value = nextValue;
                }
                if(value.compareTo(alpha) > 0)
                    alpha = value;
                if(value.compareTo(beta) >= 0){
                    killerHeuristic[maxDepth] = getIndex(g, successor);
                    break;
                }

            }
        }
        else {
            value = positiveInfinity();
            for (Game successor: successors(g, maxDepth, currentDepth, killerHeuristic[maxDepth])) {
                T nextValue = miniMax(successor, alpha, beta, maxDepth - 1, currentDepth + 1);
                if(value.compareTo(nextValue) > 0){
                    bestNextMove = successor;
                    value = nextValue;
                }
                if(value.compareTo(beta) < 0)
                    beta= value;
                if(value.compareTo(alpha) <= 0) {
                    killerHeuristic[maxDepth] = getIndex(g, successor);
                    break;
                }
            }
        }

        cache.put(g, value);
        if(currentDepth == 0){
            results.add(new MinimaxResult<>(bestNextMove, maxDepth, initialAlpha, initialBeta));
        }
        return value;
    }
}
