package ru.vsu.cs.course1;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class SnowFrame extends JFrame {
    private JPanel panelMain;
    private JSpinner spinnerSize;
    private JSpinner spinnerLevelCount;
    private JButton buttonDraw;
    private JLabel labelImg;

    public SnowFrame() {
        this.setTitle("FactorialDemo");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.pack();

        spinnerSize.setValue(300);
        spinnerLevelCount.setValue(2);

        buttonDraw.addActionListener(e -> {
            int size = 1000;
            int radius = (int) spinnerSize.getValue();
            int levelCount = (int) spinnerLevelCount.getValue();

            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_BGR);
            Graphics2D g2d = img.createGraphics();
            g2d.setColor(Color.BLACK);
            logic.paintSnow(g2d, img.getWidth(), img.getHeight(),radius, levelCount);
            labelImg.setIcon(new ImageIcon(img));

            try {
                File map = new File("map.png");
                ImageIO.write(img, "png", map);
            } catch(IOException exc) {
                System.out.println("problem saving");
            }
        });

    }}
