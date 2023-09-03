package aaron;

import driver.BotTemplate;
import driver.Game;

public class PerformanceTest {

    private static long totalTime = 0;
    private static final AaronBot aaronBot = new AaronBot();
    private static final OldBot oldBot = new OldBot();

    public static void main(String[] args) {
        Game g = new Game();
        int iterations = 20;

        long[] bot1 = new long[iterations];
        long[] bot2 = new long[iterations];
        for (int i = 0; i < iterations; i++) {
            totalTime = 0;
            nextPos(g, 8, aaronBot);
            bot1[i] = totalTime;
            System.out.println("TOTAL TIME TAKEN: " + totalTime);
            totalTime = 0;
            nextPos(g, 8, oldBot);
            bot2[i] = totalTime;
            System.out.println("TOTAL TIME TAKEN: " + totalTime);
            System.out.println("----------------");
        }

        long bot1Sum = 0;
        long bot2Sum = 0;
        for (int i = 0; i < iterations; i++) {
            bot1Sum += bot1[i];
            bot2Sum += bot2[i];
        }

        System.out.println("OVERALL BOT1: " + bot1Sum);
        System.out.println("OVERALL BOT2: " + bot2Sum);
    }

    private static void nextPos(Game initial, int depthRemaining, BotTemplate p){
        if(initial == null)
            return;
        if(depthRemaining == 0){
            long start = System.nanoTime();
            p.utility(initial, depthRemaining, 15);
            long total = System.nanoTime() - start;
//            System.out.println("OneUtil: " + total);
            totalTime += total;
            return;
        }

        for (int j = 0; j < initial.WIDTH; j++) {
            nextPos(initial.playMove(j), depthRemaining - 1, p);
        }
    }


}
