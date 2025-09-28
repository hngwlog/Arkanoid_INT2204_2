package com.raumania.main;

import com.raumania.gui.GUIRenderer;
import com.raumania.gui.Renderer;
import com.raumania.gui.screen.HomeScreen;

import java.awt.*;

public class Game {
    /**
     * Game loop.
     */
    public Game() {
        System.out.println("Game started!");
        Font font = new Font("Arial", Font.BOLD, 30);
        Color color = new Color(255, 255, 255);
        Renderer renderer = new GUIRenderer(30);
        HomeScreen menu  = new HomeScreen(renderer, "ARKANOID", 60);
        int choice = menu.run();
        renderer.clear(Color.BLACK);
        if (choice == -1) {
            renderer.drawText("You quit the menu. Bye!", 2, 1, font, color);
        }
        else {
            renderer.drawText("You selected: " + menu.getItems().get(choice) +
                    " (index " + choice + ")", 2, 1, font, color);
        }
        renderer.present();
    }
}
