package com.raumania.gui;

import javax.swing.*;
import java.awt.*;

public class test {

    public static void main(String[] args){
        //a GUI window to add components to
        JFrame f = new JFrame();

        //set window size
        f.setSize(400,500);//Do rong la 400 va chieu cao la 500
        //set frame title
        f.setTitle("Test");
        //exit when click 'X'
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //prevent frame from being resize
        //f.setResizable(false);

        ImageIcon icon = new ImageIcon("src/images/logo.png");
        f.setIconImage(icon.getImage());

        f.setVisible(true);//present (visible)

    }

}
