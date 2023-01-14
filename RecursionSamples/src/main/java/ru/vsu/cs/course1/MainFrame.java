package ru.vsu.cs.course1;

import ru.vsu.cs.util.SwingUtils;

import javax.swing.*;


public class MainFrame extends JFrame {
    private JPanel panelMain;
    private JButton buttonSierpinskiDemo;

    SnowFrame frameSierpinski = null;

    public MainFrame() {
        SwingUtils.setShowMessageDefaultErrorHandler();

        this.setTitle("MainDemo");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();


        buttonSierpinskiDemo.addActionListener(e -> {
            if (frameSierpinski == null) {
                frameSierpinski = new SnowFrame();
                makeModal(frameSierpinski);
            }
            frameSierpinski.setVisible(true);
        });
    }


    private void makeModal(JFrame jFrame) {
        jFrame.setAlwaysOnTop(true);
        jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }}
