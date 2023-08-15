import java.util.Arrays;

public class Game {
    private static final int WIN_LENGTH = 4;
    private final int WIDTH = 7;
    private final int HEIGHT = 6;
    private GameResult result;
    private boolean isFirstPlayerMove;
    private String encoding;
    private final CellState[][] board;

    Game(){
        board = new CellState[HEIGHT][WIDTH];
        result = null;
        encoding = null;
        isFirstPlayerMove = true;
        for (CellState[] cellStates : board) {
            Arrays.fill(cellStates, CellState.EMPTY);
        }
    }

    Game(Game g){
        board = new CellState[g.board.length][g.board[0].length];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(g.board[i], 0, board[i], 0, board[i].length);
        }
        isFirstPlayerMove = g.isFirstPlayerMove;
        result = g.result;
        encoding = g.encoding;
    }


    public boolean isTerminal() {
        if(result == null)
            getResult();
        return result != GameResult.IN_PROGRESS;
    }

    public boolean isFirstPlayersMove() {
        return isFirstPlayerMove;
    }

    public Game playMove(int moveIndex) {
        if(isTerminal())
            return null;
        if(moveIndex < 0 || moveIndex >= WIDTH)
            return null;
        if(board[HEIGHT - 1][moveIndex] != CellState.EMPTY)
            return null;

        Game nextPos = new Game(this);
        for (int i = 0; i < HEIGHT; i++) {
            if(nextPos.board[i][moveIndex] == CellState.EMPTY){
                nextPos.board[i][moveIndex] = getCellState(isFirstPlayersMove());
                break;
            }
        }

        nextPos.isFirstPlayerMove = !nextPos.isFirstPlayerMove;
        nextPos.result = null;
        nextPos.encoding = null;
        return nextPos;
    }

    /**
     * Gets the cell at (x, y) where (0, 0) is the bottom left corner
     * **/
    public CellState getCell(int x, int y){
        return board[y][x];
    }

    private CellState getCellState(boolean isFirstPlayerMove) {
        if(isFirstPlayerMove)
            return CellState.RED;
        else
            return CellState.YELLOW;
    }

    public GameResult getResult() {
        if(result != null)
            return result;

        //ROWS
        int[] horizontal = {0, 1};
        for (int i = 0; i < HEIGHT; i++) {
            int[] position = {i, 0};
            GameResult r = checkForWinOnRay(position, horizontal);
            if(r != null) return result = r;
        }

        //COLUMNS
        int[] vertical = {1, 0};
        for (int i = 0; i < WIDTH; i++) {
            int[] position = {0, i};
            GameResult r = checkForWinOnRay(position, vertical);
            if(r != null) return result = r;
        }

        //UP RIGHT
        int[] upRight = {1, 1};
        for (int i = 0; i < WIDTH + HEIGHT; i++) {
            int[] position = {(i < HEIGHT)? i:0, (i > HEIGHT)? i-HEIGHT:0};
            GameResult r = checkForWinOnRay(position, upRight);
            if(r != null) return result = r;
        }

        //UP LEFT
        int[] upLeft = {1, -1};
        for (int i = 0; i < WIDTH + HEIGHT; i++) {
            int[] position = {(i < HEIGHT)? i:0, (i > HEIGHT)? i-HEIGHT-1:WIDTH-1};
            GameResult r = checkForWinOnRay(position, upLeft);
            if (r != null) return result = r;
        }

        for (int i = 0; i < WIDTH; i++) {
            if(board[HEIGHT - 1][i] == CellState.EMPTY)
                return result = GameResult.IN_PROGRESS;
        }

        return result = GameResult.DRAW;
    }

    private GameResult checkForWinOnRay(int[] start, int[] direction) {
        int numConsecutive = 0;
        while (inBounds(start)){
            CellState state = board[start[0]][start[1]];
            numConsecutive = updateRunningCount(numConsecutive, state);
            GameResult r = fromRunningCount(numConsecutive);
            if(r != null)
                return r;
            updatePos(start, direction);
        }
        return null;
    }

    private void updatePos(int[] pos, int[] delta){
        pos[0] += delta[0];
        pos[1] += delta[1];
    }

    private boolean inBounds(int[] pos){
        return 0 <= pos[1] && pos[1] < WIDTH && 0 <= pos[0] && pos[0] < HEIGHT;
    }

    private GameResult fromRunningCount(int numConsecutive) {
        if(numConsecutive == WIN_LENGTH)
            return GameResult.RED_WIN;
        if(numConsecutive == -WIN_LENGTH)
            return GameResult.YELLOW_WIN;
        return null;
    }

    private int updateRunningCount(int numConsecutive, CellState state) {
        if(state == CellState.EMPTY)
            numConsecutive = 0;
        else if(state == CellState.RED)
            numConsecutive = Math.max(1, numConsecutive + 1);
        else
            numConsecutive = Math.min(-1, numConsecutive - 1);
        return numConsecutive;
    }

    @Override
    public int hashCode(){
        return stringEncoding().hashCode();
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Game other))
            return false;
        return other.stringEncoding().equals(stringEncoding());
    }

    private String stringEncoding(){
        if(encoding != null)
            return encoding;

        StringBuilder builder = new StringBuilder();
        builder.append((isFirstPlayerMove)? 'r':'y');
        for (int j = 0; j < WIDTH; j++){
            CellState state = board[0][j];
            if(state == CellState.EMPTY){
                builder.append('x');
                continue;
            }
            else if(state == CellState.RED){
                builder.append('r');
            }
            else
                builder.append('y');
            int runningCount = 0;
            for (int i = 0; i < HEIGHT; i++) {
                if(state == board[i][j])
                    runningCount++;
                else if(board[i][j] == CellState.EMPTY){
                    break;
                }
                else{
                    state = (state == CellState.RED)? CellState.YELLOW:CellState.RED;
                    builder.append(runningCount);
                    runningCount = 1;
                }
            }
            if(runningCount != 0)
                builder.append(runningCount);

        }
        return encoding = builder.toString();
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();

        for (int j = board.length - 1; j >=0; j--) {
            for (int i = 0; i < board[j].length; i++) {
                builder.append(
                        (board[j][i] == CellState.EMPTY)? "x":
                                (board[j][i] == CellState.RED)? "R":"Y"
                );
            }
            builder.append("\n");
        }
        builder.append("Player ").append(isFirstPlayersMove() ? "RED" : "YELLOW").append( " to move\n");
        return builder.toString();
    }
}
