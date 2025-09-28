package com.raumania.gui.screen;

import com.raumania.gui.GUIRenderer;
import com.raumania.gui.Renderer;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class HomeScreen implements Screen {
    private final Renderer renderer;
    private final String title;
    private final List<String> items;
    private int selected = 0;
    private final int width;

    public HomeScreen(Renderer renderer, String title, int width) {
        this.renderer = renderer;
        this.title = title;
        this.width = width;
        this.items = Arrays.asList(
                "Start Game",
                "Select Level",
                "Help",
                "Quit"
        );
    }

    /**
     * Get Items List.
     */
    public List<String> getItems() {
        return items;
    }

    /**
     * Select Items.
     * @return selected item order.
     */
    public int run(){
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                render();
                String in = readInput(sc);
                if (in == null) continue;

                switch (in) {
                    case "w":
                        selected = (selected - 1 + items.size()) % items.size();
                        break;
                    case "s":
                        selected = (selected + 1) % items.size();
                        break;
                    case "": // Enter -> select
                        return selected;
                    case "q":
                        return -1;
                    default:
                        // ignore others
                }
            }
        }
    }

    /**
     * Render Screen.
     */
    private void render() {
        Font font = new Font("Arial", Font.BOLD, 30);
        Color color = new Color(255, 255, 255);
        renderer.clear(color);
        int row = 1;

        // Title box
        String topBorder = "┌" + "─".repeat(width - 2) + "┐\n";
        String botBorder = "└" + "─".repeat(width - 2) + "┘\n";
        renderer.drawText(GUIRenderer.center(topBorder, width), row++, 1, font, color);
        String centeredTitle = "|" + GUIRenderer.center(title, width - 2) + "|\n";
        renderer.drawText(centeredTitle, row++, 1, font, color);
        renderer.drawText(GUIRenderer.center(botBorder, width),  row++, 1, font, color);

        row++; // padding
        renderer.drawText(GUIRenderer.center("Use W/S to move • Enter to select • Q to quit",
                width) + "\n", row++, 1, font, color);
        row++;

        // Menu items
        for (int i = 0; i < items.size(); i++) {
            String prefix = (i == selected) ? "▶ " : "";
            String line = String.format("%s%s", prefix, items.get(i));
            renderer.drawText(GUIRenderer.center(line, width) + "\n", row++, 1, font, color);
        }

        row++;
        renderer.present();
    }

    /**
     * Reads one "command" from user:
     * - "w" (up), "s" (down), "" (enter), "q" (quit)
     * Note: Scanner can't capture raw arrow keys portably w/o JNI/JCurses.
     */
    private String readInput(Scanner sc) {
        try {
            // Prompt on the last line
            System.out.print("> Press W/S + Enter to move; Enter to choose; Q + Enter to quit: \n");
            String line = sc.nextLine().trim().toLowerCase();
            if (line.equals("w") || line.equals("s") || line.equals("q")) return line;
            if (line.isEmpty()) return ""; // Enter
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }
}
