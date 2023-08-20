package driver;

import java.util.Scanner;

public class HumanPlayer implements Player {
    @Override
    public int getMove(Game g) {
        System.out.println("----------");
        System.out.println("0  3  6");
        System.out.print(g.toString());
        System.out.println("Enter the number of the column where you would like to play");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
}
