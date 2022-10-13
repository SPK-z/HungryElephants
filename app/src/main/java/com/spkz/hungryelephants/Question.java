package com.spkz.hungryelephants;

import java.util.Random;

public class Question {
    private final int range;
    private int[] randomNumbers;
    private boolean randomSymbol;

    public Question(int rangeInput){
        this.range = rangeInput;
        selectNumbers();
        selectSymbol();
    }

    private void selectNumbers(){
        Random rand = new Random();
        randomNumbers = new int[2];
        boolean equalNumbers = true;
        while(equalNumbers){
            randomNumbers[0] = rand.nextInt(range);
            randomNumbers[1] = rand.nextInt(range);
            equalNumbers = (randomNumbers[0] == randomNumbers[1]);
        }
    }

    private void selectSymbol(){
        Random rand = new Random();
        randomSymbol = (rand.nextInt(2) == 1);
    }

    public int[] getNumbers(){
        return randomNumbers;
    }

    public boolean getSymbol(){
        return randomSymbol;
    }

}
