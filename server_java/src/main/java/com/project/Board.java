package com.project;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class Board {

    public String[][] board = new String[4][4];

    public int[][] showBoard = new int[4][4]; 
    
    String[] colors = {"Red", "Green", "Blue", "Yellow", "Orange", "Purple", "Pink", "Black"};

    ArrayList<String> players = new ArrayList<String>();
    ArrayList<Integer> points = new ArrayList<Integer>();

    ArrayList<Integer> firstSelect = new ArrayList<Integer>();

    int turn = 1;
    int turnFlips = 0; 

    boolean endedGame = false;

    Board(){
        ArrayList<String> colorPairs = new ArrayList<>();
        firstSelect.add(0);
        firstSelect.add(0);
        points.add(0);
        points.add(0);
        for (String color : colors) {
            colorPairs.add(color);
            colorPairs.add(color);
        }

        Collections.shuffle(colorPairs, new Random());

        int index = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                showBoard[i][j] = 0;
                board[i][j] = colorPairs.get(index);
                index++;
            }
        }
    }


    

    public void newTurn(){
        turn = (turn + 1) % 2;
    }

    public boolean hasEnded(){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if(showBoard[i][j] == 0) return false;
            }
        }
        return true;
    }

    public void startTurn() {
        turn = (turn + 1) % 2;
    }
        
}
