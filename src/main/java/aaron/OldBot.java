package aaron;

import driver.BotTemplate;
import driver.CellState;
import driver.Game;
import driver.GameResult;

import java.util.ArrayList;
import java.util.Arrays;

public class OldBot extends BotTemplate<Integer> {

    private final static int ON_GROUND = 4;
    private final static int IN_AIR = 5;
    private final static int PREV_CONTROL = 5;
    private final static int NO_CONTROL = 1;
    private final static int GOOD_PARITY = 4;
    private final static int BAD_PARITY = 3;

    @Override
    public Integer utility(Game g, int depthRemaining, int currentDepth) {
        GameResult result = g.getResult();

        if(result == GameResult.RED_WIN)
            return Integer.MAX_VALUE - currentDepth;
        else if (result == GameResult.YELLOW_WIN)
            return Integer.MIN_VALUE + currentDepth;
        else if(result == GameResult.DRAW)
            return 0;


        int score = 0;
        int[][] redWeights = new int[g.WIDTH][g.HEIGHT];
        int[][] yellowWeights = new int[g.WIDTH][g.HEIGHT];
        int[] heights = new int[g.WIDTH];
        Arrays.fill(heights, g.HEIGHT);

        //Vertical
        for (int i = 0; i < g.WIDTH; i++) {
            if(g.getCell(i, g.HEIGHT - 1) != CellState.EMPTY){
                continue;
            }

            int redCount = 0;
            int yellowCount = 0;
            int[] redColumnWeight = new int[g.HEIGHT];
            int[] yellowColumnWeight = new int[g.HEIGHT];
            for (int j = 0; j < g.HEIGHT; j++) {
                CellState s = g.getCell(i, j);
                if(s == CellState.EMPTY){
                    heights[i] = j;
                }
                else if(s == CellState.RED) redCount++;
                else yellowCount++;

                if(j >= g.WIN_LENGTH){
                    s = g.getCell(i, j - g.WIN_LENGTH);
                    if (s == CellState.RED) redCount--;
                    else if(s == CellState.YELLOW) yellowCount--;
                }

                if(j >= g.WIN_LENGTH - 1){
                    for (int k = 0; k < g.WIN_LENGTH; k++) {
                        if(yellowCount == 0)
                            redColumnWeight[j - k] = Math.max(redColumnWeight[j - k], redCount);
                        else if(redCount == 0)
                            yellowColumnWeight[j - k] = Math.max(yellowColumnWeight[j - k], yellowCount);
                    }
                }

                if(s == CellState.EMPTY)
                    break;
            }

            for (int j = 0; j < g.HEIGHT; j++) {
                if(g.getCell(i, j) != CellState.EMPTY)
                    continue;

                redWeights[i][j] = redColumnWeight[j];
                yellowWeights[i][j] = yellowColumnWeight[j];
            }
        }

        //Horizontal
        for (int i = 0; i < g.HEIGHT; i++) {
            int redCount = 0;
            int yellowCount = 0;
            int[] redRowWeight = new int[g.WIDTH];
            int[] yellowRowWeight = new int[g.WIDTH];
            for (int j = 0; j < g.WIDTH; j++) {
                CellState s = g.getCell(j, i);
                if(s == CellState.RED) redCount++;
                else if(s == CellState.YELLOW) yellowCount++;

                if(j >= g.WIN_LENGTH){
                    s = g.getCell(j - g.WIN_LENGTH, i);
                    if(s == CellState.RED) redCount--;
                    else if(s == CellState.YELLOW) yellowCount--;
                }

                if(j >= g.WIN_LENGTH - 1){
                    for (int k = 0; k < g.WIN_LENGTH; k++) {
                        if(yellowCount == 0)
                            redRowWeight[j - k] = Math.max(redRowWeight[j - k], redCount);
                        else if(redCount == 0)
                            yellowRowWeight[j - k] = Math.max(yellowRowWeight[j - k], yellowCount);
                    }
                }
            }

            for (int j = 0; j < g.WIDTH; j++) {
                if(g.getCell(j, i) != CellState.EMPTY)
                    continue;

                redWeights[j][i] = Math.max(redWeights[j][i], redRowWeight[j]);
                yellowWeights[j][i] = Math.max(yellowWeights[j][i], yellowRowWeight[j]);
            }
        }

        //Up Right
        for (int i = 0; i < g.WIDTH + g.HEIGHT; i++) {
            int[] position = {(i > g.HEIGHT)? i-g.HEIGHT:0, (i < g.HEIGHT)? i:0};
            int redCount = 0;
            int yellowCount = 0;
            int[] redDiagonal = new int[Math.max(g.WIDTH, g.HEIGHT)];
            int[] yellowDiagonal = new int[Math.max(g.WIDTH, g.HEIGHT)];
            for (int j = 0;position[0] + j < g.WIDTH && position[1] + j < g.HEIGHT; j++) {
                CellState s = g.getCell(position[0] + j, position[1] + j);

                if(s == CellState.RED) redCount++;
                else if(s == CellState.YELLOW) yellowCount++;

                if(j >= g.WIN_LENGTH){
                    s = g.getCell(position[0] + j - g.WIN_LENGTH, position[1] + j - g.WIN_LENGTH);
                    if(s == CellState.RED) redCount--;
                    else if(s == CellState.YELLOW) yellowCount--;
                }

                if(j >= g.WIN_LENGTH - 1){
                    for (int k = 0; k < g.WIN_LENGTH; k++) {
                        if(yellowCount == 0)
                            redDiagonal[j - k] = Math.max(redDiagonal[j - k], redCount);
                        else if(redCount == 0)
                            yellowDiagonal[j - k] = Math.max(yellowDiagonal[j - k], yellowCount);
                    }
                }
            }

            for (int j = 0;position[0] + j < g.WIDTH && position[1] + j < g.HEIGHT; j++) {
                int x = position[0] + j;
                int y = position[1] + j;
                if(g.getCell(x,y) != CellState.EMPTY)
                    continue;
                redWeights[x][y] =
                        Math.max(redWeights[x][y], redDiagonal[j]);
                yellowWeights[x][y] =
                        Math.max(yellowWeights[x][y], yellowDiagonal[j]);
            }
        }

        //Up Left
        for (int i = 0; i < g.WIDTH + g.HEIGHT; i++) {
            int[] position = {(i > g.HEIGHT)? i-g.HEIGHT:0, (i < g.HEIGHT)? i:0};
            int redCount = 0;
            int yellowCount = 0;
            int[] redDiagonal = new int[Math.max(g.WIDTH, g.HEIGHT)];
            int[] yellowDiagonal = new int[Math.max(g.WIDTH, g.HEIGHT)];
            for (int j = 0; position[0] - j >= 0 && position[1] + j < g.HEIGHT; j++) {
                CellState s = g.getCell(position[0] - j, position[1] + j);

                if(s == CellState.RED) redCount++;
                else if(s == CellState.YELLOW) yellowCount++;

                if(j >= g.WIN_LENGTH){
                    s = g.getCell(position[0] - j + g.WIN_LENGTH, position[1] + j - g.WIN_LENGTH);
                    if(s == CellState.RED) redCount--;
                    else if(s == CellState.YELLOW) yellowCount--;
                }

                if(j >= g.WIN_LENGTH - 1){
                    for (int k = 0; k < g.WIN_LENGTH; k++) {
                        if(yellowCount == 0)
                            redDiagonal[j - k] = Math.max(redDiagonal[j - k], redCount);
                        else if(redCount == 0)
                            yellowDiagonal[j - k] = Math.max(yellowDiagonal[j - k], yellowCount);
                    }
                }
            }

            for (int j = 0; position[0] - j >= 0 && position[1] + j < g.HEIGHT; j++) {
                int x = position[0] - j;
                int y = position[1] + j;
                if(g.getCell(x,y) != CellState.EMPTY)
                    continue;
                redWeights[x][y] =
                        Math.max(redWeights[x][y], redDiagonal[j]);
                yellowWeights[x][y] =
                        Math.max(yellowWeights[x][y], yellowDiagonal[j]);
            }

        }

        //Totalling
        for (int i = 0; i < g.WIDTH; i++) {
            for (int j = 0; j < g.HEIGHT; j++) {
                if(g.getCell(i, j) != CellState.EMPTY)
                    continue;

                int distanceToGround = j - heights[i];
                int shiftedDistance = g.HEIGHT - distanceToGround;
                boolean shouldBreak = redWeights[i][j] == g.WIN_LENGTH - 1 && yellowWeights[i][j] == g.WIN_LENGTH - 1;
                if(j == 0 || yellowWeights[i][j - 1] != g.WIN_LENGTH-1){
                    boolean controlsPrevious = j > 0 && redWeights[i][j-1] == g.WIN_LENGTH - 1;
                    boolean parityMatters = (g.HEIGHT&1) == 0 && (j&1)==0;
                    int multiplier = ((j == heights[i])? ON_GROUND:IN_AIR) *
                            (controlsPrevious? PREV_CONTROL:NO_CONTROL) *
                            (parityMatters? GOOD_PARITY:BAD_PARITY);

                    score += redWeights[i][j] * redWeights[i][j] * shiftedDistance * multiplier;
                    if(redWeights[i][j] == g.WIN_LENGTH - 1 && controlsPrevious)
                        shouldBreak = true;
                }

                if(j == 0 || redWeights[i][j - 1] != g.WIN_LENGTH-1){
                    boolean controlsPrevious = j > 0 && yellowWeights[i][j-1] == g.WIN_LENGTH - 1;
                    boolean parityMatters = (g.HEIGHT&1) == 0 && (j&1)==1;
                    int multiplier = ((j == heights[i])? ON_GROUND:IN_AIR) *
                            ((controlsPrevious)? PREV_CONTROL:NO_CONTROL) *
                            (parityMatters? GOOD_PARITY:BAD_PARITY);

                    score -= yellowWeights[i][j] * yellowWeights[i][j] * shiftedDistance * multiplier;
                    if(yellowWeights[i][j] == g.WIN_LENGTH - 1 && controlsPrevious)
                        shouldBreak = true;
                }

                if(shouldBreak)
                    break;
            }
        }
        return score;
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
    public int getMaxDepth(Game g) {
        int count = 0;

        for (int i = 0; i < g.WIDTH; i++) {
            if(g.getCell(i, g.HEIGHT - 1) == CellState.EMPTY)
                count++;
        }

        int maxDepth = g.HEIGHT * count;
        int o = 0;
        long cap = 100_000_000_000L;
        while (cap > 0 && o <= maxDepth){
            cap /= count;
            o++;
        }

        return o;
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
