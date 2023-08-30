import driver.CellState;
import driver.Game;
import driver.GameResult;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameTransitionTest {
    Game g = new Game(2, 4, 6);

    @Test
    public void checkGravityYellow(){
        Game n = g.playMove(0).playMove(0);

        assertEquals(n.getCell(0, 0), CellState.RED);
        assertEquals(n.getCell(0, 1), CellState.YELLOW);
        assertEquals(n.getCell(1, 0), CellState.EMPTY);
        assertEquals(n.getCell(2, 0), CellState.EMPTY);
        assertEquals(n.getCell(3, 0), CellState.EMPTY);
    }

    @Test
    public void checkGravityRed(){
        Game n = g.playMove(0).playMove(3).playMove(3);

        assertEquals(n.getCell(0, 0), CellState.RED);
        assertEquals(n.getCell(1, 0), CellState.EMPTY);
        assertEquals(n.getCell(2, 0), CellState.EMPTY);
        assertEquals(n.getCell(3, 0), CellState.YELLOW);
        assertEquals(n.getCell(3, 1), CellState.RED);
    }

    @Test
    public void checkHorizontalWin(){
        Game n = g.playMove(0).playMove(3).playMove(1);

        assertEquals(n.getResult(), GameResult.RED_WIN);
    }

    @Test
    public void checkVerticalWin(){
        Game n = g.playMove(0).playMove(3).playMove(2).playMove(3);

        assertEquals(n.getResult(), GameResult.YELLOW_WIN);
    }

    @Test
    public void checkDiagonalWins(){
        Game n = g.playMove(0).playMove(1).playMove(1);
        assertEquals(n.getResult(), GameResult.RED_WIN);

        n = g.playMove(0).playMove(0).playMove(3).playMove(1);
        assertEquals(n.getResult(), GameResult.YELLOW_WIN);
    }

    @Test
    public void checkDrawCondition(){
        Game f = new Game(2, 3, 1);
        Game h = f.playMove(0).playMove(1).playMove(2);

        assertEquals(h.getResult(), GameResult.DRAW);
    }

    @Test
    public void checkInProgress(){
        assertEquals(g.getResult(), GameResult.IN_PROGRESS);
        assertEquals(g.playMove(0).getResult(), GameResult.IN_PROGRESS);
    }
}
