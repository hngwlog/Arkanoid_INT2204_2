package com.raumania.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

/**
 * Rendering abstraction for the game.
 *
 * <p>This interface decouples the game logic (eg., Ball, Paddle, Brick) from the
 * actual rendering technology (Java Swing). Concrete implementations
 * will decide how to draw shapes and images on screen.</p>
 */
public interface Renderer {

    /**
     * Clears the screen or current buffer before drawing new frame.
     *
     * @param color background color
     */
    void clear(Color color);

    /**
     * Draws a filled rectangle.
     *
     * @param x x-coordinate (top-left)
     * @param y y-coordinate (top-left)
     * @param width rectangle width
     * @param height rectangle height
     * @param color fill color
     */
    void fillRect(double x, double y, double width, double height, Color color);

    /**
     * Draws a circle.
     *
     * @param cx x-coordinate of center
     * @param cy y-coordinate of center
     * @param radius circle radius
     * @param color fill color
     */
    void fillCircle(double cx, double cy, double radius, Color color);

    /**
     * Draws an image.
     *
     * @param img image to draw
     * @param x x-coordinate (top-left)
     * @param y y-coordinate (top-left)
     * @param width draw width
     * @param height draw height
     */
    void drawImage(Image img, double x, double y, double width, double height);

    /**
     * Draws a string of text.
     *
     * @param text the string to draw
     * @param x x-coordinate (baseline start)
     * @param y y-coordinate (baseline start)
     * @param font font to use
     * @param color text color
     */
    void drawText(String text, double x, double y, Font font, Color color);

    /**
     * Finalizes and presents the frame to the display.
     */
    void present();
}
