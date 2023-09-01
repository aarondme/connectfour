package driver;

public class Game {
    public final int WIN_LENGTH;
    public final int WIDTH;
    public final int HEIGHT;
    private static final int ENCODED_YELLOW = 2;
    private static final int ENCODED_RED = 3;
    private static final int ENCODED_BLANK = 0;
    private final GameResult result;
    private final String encoding;

    public Game(int win_length, int width, int height){
        result = (width > 0 || height > 0)? GameResult.IN_PROGRESS: GameResult.DRAW;
        WIN_LENGTH = win_length;
        WIDTH = width;
        HEIGHT = height;
        encoding = defaultEncoding();
    }
    public Game(){
        result = GameResult.IN_PROGRESS;
        WIN_LENGTH = 4;
        WIDTH = 7;
        HEIGHT = 6;
        encoding = defaultEncoding();
    }

    private String defaultEncoding() {
        StringBuilder builder = new StringBuilder();
        int first = ENCODED_RED << 14;
        builder.append((char) first);
        int numCells = WIDTH * HEIGHT - 7;
        while (numCells > 0){
            builder.append((char) 0);
            numCells -= 8;
        }
        return builder.toString();
    }

    public Game(Game g){
        result = g.result;
        encoding = g.encoding;
        WIN_LENGTH = g.WIN_LENGTH;
        WIDTH = g.WIDTH;
        HEIGHT = g.HEIGHT;
    }

    private Game(Game g, int moveIndex){
        int heightToSet = -1;
        WIN_LENGTH = g.WIN_LENGTH;
        WIDTH = g.WIDTH;
        HEIGHT = g.HEIGHT;
        CellState s = getPlayerCell(g.isFirstPlayersMove());
        for (int i = 0; i < g.HEIGHT; i++) {
            if(g.getCell(moveIndex, i) == CellState.EMPTY){
                heightToSet = i;
                break;
            }
        }
        String tempEncoding = g.encoding;
        tempEncoding = setCell(moveIndex, heightToSet, s, tempEncoding);
        tempEncoding = flipTurn(tempEncoding);
        encoding = tempEncoding;
        result = checkForWinInvolving(moveIndex, heightToSet);
    }

    public boolean isTerminal() {
        return result != GameResult.IN_PROGRESS;
    }

    public boolean isFirstPlayersMove() {
        int first = encoding.charAt(0);
        return (first & (1 << 14)) > 0;
    }

    public Game playMove(int moveIndex) {
        if(isTerminal())
            return null;
        if(moveIndex < 0 || moveIndex >= WIDTH)
            return null;
        if(getCell(moveIndex, HEIGHT-1) != CellState.EMPTY)
            return null;

       return new Game(this, moveIndex);
    }

    /**
     * Gets the cell at (x, y) where (0, 0) is the bottom left corner
     * **/
    public CellState getCell(int x, int y){
        int index = toBitmaskIndex(x, y);
        int charIndex = index >> 3; //Get character in which the cell is stored
        int indexInChar = index & 0b111; //Get the index in the character where the cell is stored
        int shiftAmount = (7 - indexInChar) << 1;
        int c = encoding.charAt(charIndex);
        c = (c >> shiftAmount) & 0b11; //Shift the indices to the 0th and 1st bits, and extract them
        if(c == ENCODED_YELLOW)
            return CellState.YELLOW;
        else if(c == ENCODED_RED)
            return CellState.RED;
        return CellState.EMPTY;
    }

    private String setCell(int x, int y, CellState state, String prevMask){
        int index = toBitmaskIndex(x, y);
        int charIndex = index >> 3; //Get character in which the cell is stored
        int indexInChar = index & 0b111; //Get the index in the character where the cell is stored
        int c = prevMask.charAt(charIndex);
        int shiftAmount = (7 - indexInChar) << 1;
        c = (0b11 << shiftAmount) | c; //Set the corresponding indices to 0b11
        int valToSet = (state == CellState.EMPTY)? ENCODED_BLANK: (state == CellState.RED)? ENCODED_RED:ENCODED_YELLOW;
        valToSet = valToSet ^ 0b11;
        c = (valToSet << shiftAmount) ^ c; //XOR with the opposite of the values we want to set it to.

        return prevMask.substring(0, charIndex) +
                (char) c +
                prevMask.substring(charIndex + 1);
    }

    private String flipTurn(String prevMask){
        int c = prevMask.charAt(0);
        c = (1 << 14) ^ c;
        return (char) c + prevMask.substring(1);
    }

    private int toBitmaskIndex(int x, int y){
        return x * HEIGHT + y + 1;
    }

    private CellState getPlayerCell(boolean isFirstPlayerMove) {
        if(isFirstPlayerMove)
            return CellState.RED;
        else
            return CellState.YELLOW;
    }

    private GameResult computeResult(){
        //Could be useful later for starting with partially filled boards

        //ROWS
        int[] horizontal = {1, 0};
        for (int i = 0; i < HEIGHT; i++) {
            int[] position = {0, i};
            GameResult r = checkForWinOnRay(position, horizontal);
            if(r != null) return r;
        }

        //COLUMNS
        int[] vertical = {0, 1};
        for (int i = 0; i < WIDTH; i++) {
            int[] position = {i, 0};
            GameResult r = checkForWinOnRay(position, vertical);
            if(r != null) return r;
        }

        //UP RIGHT
        int[] upRight = {1, 1};
        for (int i = 0; i < WIDTH + HEIGHT; i++) {
            int[] position = {(i > HEIGHT)? i-HEIGHT:0, (i < HEIGHT)? i:0};
            GameResult r = checkForWinOnRay(position, upRight);
            if(r != null) return r;
        }

        //UP LEFT
        int[] upLeft = {-1, 1};
        for (int i = 0; i < WIDTH + HEIGHT; i++) {
            int[] position = {(i > HEIGHT)? i-HEIGHT-1:WIDTH-1, (i < HEIGHT)? i:0};
            GameResult r = checkForWinOnRay(position, upLeft);
            if (r != null) return r;
        }

        for (int i = 0; i < WIDTH; i++) {
            if(getCell(i, HEIGHT-1) == CellState.EMPTY)
                return GameResult.IN_PROGRESS;
        }

        return GameResult.DRAW;
    }

    public GameResult getResult() {
        return result;
    }

    private GameResult checkForWinInvolving(int moveIndex, int heightToSet) {
        GameResult r;
        //HORIZONTAL
        if((r = checkForWinOnRay(new int[]{0, heightToSet}, new int[]{1, 0})) != null)
            return r;
        //VERTICAL
        if(heightToSet >= WIN_LENGTH-1 && (r = checkForWinOnRay(new int[]{moveIndex, 0}, new int[]{0, 1})) != null)
            return r;

        //UP RIGHT
        int min = Math.min(moveIndex, heightToSet);
        if((r = checkForWinOnRay(new int[]{moveIndex - min, heightToSet - min}, new int[]{1, 1})) != null)
            return r;

        //UP LEFT
        min = Math.min(WIDTH - moveIndex - 1, heightToSet);
        if((r = checkForWinOnRay(new int[]{moveIndex + min, heightToSet - min}, new int[]{-1, 1})) != null)
            return r;

        for (int i = 0; i < WIDTH; i++) {
            if(getCell(i, HEIGHT-1) == CellState.EMPTY)
                return GameResult.IN_PROGRESS;
        }

        return GameResult.DRAW;
    }

    private GameResult checkForWinOnRay(int[] start, int[] direction) {
        int numConsecutive = 0;
        while (inBounds(start)){
            CellState state = getCell(start[0], start[1]);
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
        return 0 <= pos[0] && pos[0] < WIDTH && 0 <= pos[1] && pos[1] < HEIGHT;
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
        return encoding.hashCode();
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Game other))
            return false;
        return other.encoding.equals(encoding);
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();

        for (int j = HEIGHT - 1; j >=0; j--) {
            for (int i = 0; i < WIDTH; i++) {
                CellState state = getCell(i, j);
                builder.append(
                        (state == CellState.EMPTY)? "x":
                                (state == CellState.RED)? "R":"Y"
                );
            }
            builder.append("\n");
        }
        builder.append("Player ").append(isFirstPlayersMove() ? "RED" : "YELLOW").append( " to move\n");
        return builder.toString();
    }
}
