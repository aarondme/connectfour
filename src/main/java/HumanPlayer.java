import java.util.Scanner;

public class HumanPlayer implements Player {
    @Override
    public int getMove(Game g) {

        System.out.println("----------");
        System.out.println("0  3  6");
        for (int j = g.board.length - 1; j >=0; j--) {
            for (int i = 0; i < g.board[j].length; i++) {
                System.out.print(
                        (g.board[j][i] == CellState.EMPTY)? "x":
                                (g.board[j][i] == CellState.RED)? "R":"Y"
                );
            }
            System.out.print("\n");
        }
        System.out.println("You are player: " + (g.isFirstPlayersMove()? "RED":"YELLOW"));
        System.out.println("Enter the number of the column where you would like to play");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
}
