package driver;

import aaron.AaronBot;

public class Driver {

    public static void main(String[] args) {
        Player playerOne = new BasicBot();
        Player playerTwo = new AaronBot();
        Game g = new Game();

        while (!g.isTerminal()){
            int moveIndex;
            boolean isFirstPlayerMove = g.isFirstPlayersMove();
            if (isFirstPlayerMove)
                moveIndex = playerOne.getMove(new Game(g));
            else
                moveIndex = playerTwo.getMove(new Game(g));
            System.out.println(moveIndex + " " + isFirstPlayerMove);
            Game next = g.playMove(moveIndex);
            if(next == null){
                System.out.println("Player " + ((isFirstPlayerMove)? "1":"2") + " made an illegal move");
                break;
            }
            g = next;
        }
        System.out.println(g);
        System.out.println(g.getResult());
    }
}
