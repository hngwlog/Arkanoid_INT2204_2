package com.raumania.gui.screen;

import java.util.Scanner;

public class homeScreen {
    public homeScreen() {
        Scanner in  = new Scanner(System.in);
        System.out.println("Welcome to the Home screen!");
        System.out.println("Start");
        System.out.println("Config");
        int choice =  in.nextInt();
        if (choice == 1) {
            levelSelectScreen level = new levelSelectScreen();
        }
        else {
            config config = new config();
        }
    }
}
