package com.raumania.utils;

import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

import static com.raumania.utils.Constants.WINDOW_WIDTH;

public class UIUtils {

    /**
     * Create a new x-centered Button.
     * @param title button title
     * @param y button Y
     * @param xScale button ScaleX
     * @param yScale button ScaleY
     * @return new button
     */
    public static Button centerButton(String title, double y, double xScale, double yScale) {
        Button button =  newButton(title, 1, y, xScale, yScale);
        button.layoutBoundsProperty().addListener((
                obs, oldVal, newVal) -> {
            button.setLayoutX((WINDOW_WIDTH - newVal.getWidth())/2);});
        return button;
    }

    /**
     * Create a new Button.
     * @param title button title
     * @param x button X
     * @param y button Y
     * @param xScale button scaleX
     * @param yScale button scaleY
     * @return new button
     */
    public static Button newButton(String title, double x, double y, double xScale, double yScale) {
        Button button =  new Button(title);
        button.setScaleX(xScale);
        button.setScaleY(yScale);
        button.setLayoutX(x);
        button.setLayoutY(y);
        return button;
    }

    /**
     * Create a new Text.
     * @param title text title
     * @param x text x
     * @param y text y
     * @param xScale text scaleX
     * @param yScale text scaleY
     * @return new Text
     */
    public static Text newText(String title, double x, double y, double xScale, double yScale) {
        Text text = new Text(title);
        text.setX(x);
        text.setY(y);
        text.setScaleX(xScale);
        text.setScaleY(yScale);
        return text;
    }

    /**
     * Create a new x-centered Text.
     * @param title text title
     * @param y text y
     * @param xScale text scaleX
     * @param yScale text scaleY
     * @return new Text
     */
    public static Text centerText(String title, double y,  double xScale, double yScale) {
        Text text = newText(title, 1, y, xScale, yScale);
        double textX = (WINDOW_WIDTH - text.getLayoutBounds().getWidth())/2;
        text.setX(textX);
        return text;
    }

    /**
     * Create a new Slider.
     * @param minSlider slider min value
     * @param startSlider slider start value
     * @param maxSlider slider max value
     * @param x slider x
     * @param y slider y
     * @param xScale slider scaleX
     * @param yScale slider scaleY
     * @param label setShowTickLabels for slider
     * @param mark setShowTickMarks for slider
     * @return new Slider
     */
    public static Slider newSlider(int minSlider, int startSlider, int maxSlider, double x, double y,
                                   double xScale, double yScale, boolean label, boolean mark) {
        Slider slider = new Slider(minSlider, maxSlider,startSlider);
        slider.setLayoutX(x);
        slider.setLayoutY(y);
        slider.setScaleX(xScale);
        slider.setScaleY(yScale);
        slider.setShowTickLabels(label);
        slider.setShowTickMarks(mark);
        return slider;
    }

}
