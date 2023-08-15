
public abstract class BotTemplate<T extends Comparable<T>> implements Player {
    private record MoveWithValue<T>(T value, int index) {
    }

    abstract T utility(Game g, int depthRemaining);

    abstract int[] iterationOrder();

    abstract int getMaxDepth();

    @Override
    public int getMove(Game state){
        MoveWithValue<T> bestMove = miniMax(state, null, null, getMaxDepth());
        return bestMove.index;
    }

    MoveWithValue<T> miniMax(Game g, T alpha,T beta, int maxDepth){
        if(maxDepth == 0 || g.isTerminal())
            return new MoveWithValue<>(utility(g, maxDepth), -1);

        MoveWithValue<T> bestMove = null;
        if(g.isFirstPlayersMove()){
            for (int index: iterationOrder()) {
                Game successor = g.playMove(index);
                if(successor == null)
                    continue;
                MoveWithValue<T> bestNextMove = miniMax(successor, alpha, beta, maxDepth - 1);
                if(bestMove == null || bestMove.value.compareTo(bestNextMove.value) < 0)
                    bestMove = new MoveWithValue<>(bestNextMove.value, index);
                if(beta != null && bestMove.value.compareTo(beta) >= 0)
                    break;
                if(alpha == null || bestMove.value.compareTo(alpha) < 0)
                    alpha = bestMove.value;
            }
        }
        else {
            for (int index: iterationOrder()) {
                Game successor = g.playMove(index);
                if(successor == null)
                    continue;
                MoveWithValue<T> nextValue = miniMax(successor, alpha, beta, maxDepth - 1);
                if(bestMove == null || bestMove.value.compareTo(nextValue.value) > 0)
                    bestMove = new MoveWithValue<>(nextValue.value, index);
                if(alpha != null && bestMove.value.compareTo(alpha) <= 0)
                    break;
                if(beta == null || bestMove.value.compareTo(beta) > 0)
                    beta= bestMove.value;
            }
        }

        return bestMove;
    }
}
