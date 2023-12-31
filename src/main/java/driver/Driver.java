package driver;

import aaron.AaronBot;

public class Driver {

    public static void main(String[] args) {
        Player playerOne = new BasicBot();
        Player playerTwo = new AaronBot();
        Game g = new Game();

//        playGame(playerOne, playerTwo, g);
        runBotTest(playerOne, playerTwo, g);
    }

    private static void runBotTest(Player playerOne, Player playerTwo, Game g) {
        int p1Win = 0;
        int draw = 0;
        int p1Loss = 0;

        for (int i = -1; i < g.WIDTH; i++) {
            Game n = (i == -1)? g : g.playMove(i);
            System.out.println("Initial i: " + i + ", Player One: RED");

            GameResult r = playGame(playerOne, playerTwo, n);
            if(r == GameResult.DRAW) draw++;
            else if(r == GameResult.RED_WIN) p1Win++;
            else p1Loss++;

            System.out.println("Initial i: " + i + ", Player Two: Red");
            GameResult s = playGame(playerTwo, playerTwo, n);
            if(s == GameResult.DRAW) draw++;
            else if(s == GameResult.YELLOW_WIN) p1Win++;
            else p1Loss++;
        }

        System.out.println("Player One Record: " + p1Win + "-" + draw + "-" + p1Loss);
    }

    private static GameResult playGame(Player playerOne, Player playerTwo, Game g) {
        while (!g.isTerminal()){
            int moveIndex;
            boolean isFirstPlayerMove = g.isFirstPlayersMove();
            if (isFirstPlayerMove)
                moveIndex = playerOne.getMove(new Game(g));
            else
                moveIndex = playerTwo.getMove(new Game(g));
            System.out.print(moveIndex + (isFirstPlayerMove? ".":"/"));
            Game next = g.playMove(moveIndex);
            if(next == null){
                System.out.println("Player " + ((isFirstPlayerMove)? "1":"2") + " made an illegal move");
                break;
            }
            g = next;
        }
        System.out.print('\n');
        System.out.println(g);
        return g.getResult();
    }


}
