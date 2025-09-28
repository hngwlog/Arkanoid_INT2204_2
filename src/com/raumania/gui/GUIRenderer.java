package com.raumania.gui;

import javax.swing.*;
import java.awt.*;

public class GUIRenderer implements Renderer {

    private JFrame frame;

    public GUIRenderer() {
        this.frame = new JFrame();
        this.frame.setLayout(null);
        this.frame.setTitle("Arkanoid");
    }

    public GUIRenderer(JFrame _frame) {
        this.frame = _frame;
    }

    /**
     * Frame getter.
     * @return this.frame
     */
    public JFrame getFrame() {
        return this.frame;
    }

    /**
     * Set frame background color.
     */
    public void colorSet(Color color) {
        frame.getContentPane().setBackground(color);
    }

    /**
     * set window size.
     * @param width window width
     * @param height window height
     */
    public void setSize(int width, int height) {
        this.frame.setSize(width, height);
    }

    /**
     * set window title.
     * @param title window title
     */
    public void setTitle(String title) {
        this.frame.setTitle(title);
    }

    /**
     * click 'X' to exit.
     */
    public void setExit() {
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void addButton(JButton button) {
        this.frame.add(button);
    }

    public void clear(Color color) {

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
        JLabel txt = new JLabel();
        txt.setText(text);
        txt.setFont(font);
        txt.setForeground(color);
        FontMetrics metrics = txt.getFontMetrics(font);
        txt.setBounds(x ,y, metrics.stringWidth(text), metrics.getHeight());
        System.out.println(metrics.stringWidth(text)+ " "+ metrics.getHeight());
        frame.add(txt);
    }

    /**
     * Finalizes and presents the frame to the display.
     */
    public void present() {
        frame.setVisible(true);
    }

}
