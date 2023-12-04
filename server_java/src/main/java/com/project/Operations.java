package com.project;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class Operations {

    public String[][] board = new String[4][4];

    public int[][] showBoard = new int[4][4]; // 0: Don't show, 1: Show permanently, 2: Show temporarily
    
    String[] colors = {"Red", "Green", "Blue", "Yellow", "Orange", "Purple", "Pink", "Black"};

    ArrayList<String> players = new ArrayList<String>();
    ArrayList<Integer> points = new ArrayList<Integer>();

    ArrayList<Integer> firstSelect = new ArrayList<Integer>();

    int turn = 1;
    int turnFlips = 0; 

    boolean endedGame = false;

    Operations(){
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

    public void printBoard() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public boolean flipCard(int row, int col){
        if(showBoard[row][col] == 0 && turnFlips <= 2){
            ++turnFlips;
            showBoard[row][col] = 2;
            if(turnFlips == 2) return true;
            else{
                 firstSelect.set(0, row);
                 firstSelect.set(1, col);
            }
        }
        return false;
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
        
}
