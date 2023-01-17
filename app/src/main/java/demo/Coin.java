package demo;

import java.util.Arrays;

public class Coin{
    public static int minCoins(int[] coins, int target) {
        int numCoins = 0;
        int remaining = target;
        Arrays.sort(coins);

        for (int i = coins.length - 1; i >= 0; i--) {
            while (remaining >= coins[i]) {
                remaining -= coins[i];
                numCoins++;
            }
        }
        return numCoins;
    }
}

