package driver;

public class Game {
    public final int WIN_LENGTH;
    public final int WIDTH;
    public final int HEIGHT;
    private final boolean isRedTurn;
    private final GameResult result;
    private final String encoding;

    public Game(int win_length, int width, int height){
        result = (width > 0 || height > 0)? GameResult.IN_PROGRESS: GameResult.DRAW;
        WIN_LENGTH = win_length;
        WIDTH = width;
        HEIGHT = height;
        isRedTurn = true;
        encoding = defaultEncoding();
    }
    public Game(){
        result = GameResult.IN_PROGRESS;
        WIN_LENGTH = 4;
        WIDTH = 7;
        HEIGHT = 6;
        isRedTurn = true;
        encoding = defaultEncoding();
    }

    private String defaultEncoding() {
        if(HEIGHT < 8){
            StringBuilder builder = new StringBuilder();
            builder.append(String.valueOf((char) (0x0101)).repeat(WIDTH / 2));
            if((WIDTH & 1) == 1)
                builder.append((char) 0x0100);

            return builder.toString();
        }
        return null; //will do when/if necessary.
    }

    public Game(Game g){
        result = g.result;
        encoding = g.encoding;
        WIN_LENGTH = g.WIN_LENGTH;
        WIDTH = g.WIDTH;
        HEIGHT = g.HEIGHT;
        isRedTurn = g.isRedTurn;
    }

    private Game(Game g, int moveIndex){
        int heightToSet = g.getNumTokens(moveIndex);
        WIN_LENGTH = g.WIN_LENGTH;
        WIDTH = g.WIDTH;
        HEIGHT = g.HEIGHT;
        CellState s = getPlayerCell(g.isFirstPlayersMove());
        String tempEncoding = g.encoding;
        tempEncoding = setCell(moveIndex, heightToSet, s, tempEncoding);
        encoding = tempEncoding;
        result = checkForWinInvolving(moveIndex, heightToSet);
        isRedTurn = !g.isRedTurn;
    }

    public boolean isTerminal() {
        return result != GameResult.IN_PROGRESS;
    }

    public boolean isFirstPlayersMove() {
        return isRedTurn;
    }

    public int getNumTokens(int column){
        int charIndex = column >> 1; //Get character in which the cell is stored
        int c = encoding.charAt(charIndex);
        if((column & 1) == 0){
            c = (c & 0xFF00) >> 8;
        }
        else{
            c = c & 0xFF;
        }
        int out = 0;
        while (c > 1){
            c = c >> 1;
            out++;
        }
        return out;
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
        int charIndex = x >> 1; //Get character in which the cell is stored
        int c = encoding.charAt(charIndex);
        if((x & 1) == 0){
            c = (c & 0xFF00) >> 8;
        }
        else{
            c = c & 0xFF;
        }

        int bitToCheck = 1 << y;
        if((bitToCheck << 1) > c)
            return CellState.EMPTY;
        if((bitToCheck & c) == 0)
            return CellState.YELLOW;
        return  CellState.RED;
    }

    //Assumes y is the cell at the top of the column; state is either red or yellow
    private String setCell(int x, int y, CellState state, String prevMask){
        int charIndex = x >> 1; //Get character in which the cell is stored
        int c = prevMask.charAt(charIndex);

        int valToSet = 0b10 + ((state == CellState.RED)? 0:1); //Last bit is flipped to make XOR work nicely
        if((x & 1) == 0)
            valToSet = valToSet << 8;

        valToSet = valToSet << y;
        c = c ^ valToSet;


        return prevMask.substring(0, charIndex) +
                (char) c +
                prevMask.substring(charIndex + 1);
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

        if(isTerminal())
            builder.append(getResult()).append("\n");
        else
            builder.append("Player ").append(isFirstPlayersMove() ? "RED" : "YELLOW").append( " to move\n");
        return builder.toString();
    }
}
