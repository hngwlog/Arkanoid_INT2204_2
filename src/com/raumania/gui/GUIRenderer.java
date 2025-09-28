package com.raumania.gui;

import java.awt.*;

public class GUIRenderer implements Renderer {

    private static final String ESC = "\u001B[";
    private final StringBuilder frame = new StringBuilder();
    private final int height;

    public GUIRenderer(int height) {
        this.height = height;
    }

    /**
     * height getter.
     * @return this.height
     */
    public int getHeight() {
        return this.height;
    }

    public void clear(Color color) {
        frame.setLength(0);
        frame.append(ESC).append("2J").append(ESC).append("1H");
    }

    /**
     * Draws a filled rectangle.
     *
     * @param x x-coordinate (top-left)
     * @param y y-coordinate (top-left)
     * @param width rectangle width
     * @param height rectangle height
     * @param color fill color
     */
    public void fillRect(double x, double y, double width, double height, Color color) {

    }

    /**
     * Draws a circle.
     *
     * @param cx x-coordinate of center
     * @param cy y-coordinate of center
     * @param radius circle radius
     * @param color fill color
     */
    public void fillCircle(double cx, double cy, double radius, Color color) {

    }

    /**
     * Draws an image.
     *
     * @param img image to draw
     * @param x x-coordinate (top-left)
     * @param y y-coordinate (top-left)
     * @param width draw width
     * @param height draw height
     */
    public void drawImage(Image img, double x, double y, double width, double height) {

    }

    /**
     * Draws a string of text.
     *
     * @param text the string to draw
     * @param x x-coordinate (baseline start)
     * @param y y-coordinate (baseline start)
     * @param font font to use
     * @param color text color
     */
    public void drawText(String text, int x, int y, Font font, Color color) {
        frame.append(ESC).append(x + 1).append(";").append(y).append("H").append(text);
    }

    /**
     * Finalizes and presents the frame to the display.
     */
    public void present() {
        System.out.println(frame);
        System.out.flush();
    }

    /**
     * center making String
     * @param s String
     * @param width screen width
     * @return center-ed String
     */
    public static String center(String s, int width) {
        if (s.length() >= width) return s;
        int pad = (width - s.length()) / 2;
        String spaces = " ".repeat(Math.max(0, pad));
        return spaces + s + spaces;
    }

}
