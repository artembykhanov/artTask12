package ru.vsu.cs.course1;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;


public class logic {
    private static void paintSnowLevel(Graphics2D g2d, double x, double y, double radius, double width, double height, int level) {

        double sin60 = Math.sqrt(3) * radius / 2;

        if (level > 0) {
            paintSnowLevel(g2d, x, y, radius, width, height, level - 1);
            paintSnowLevel(g2d, x - radius, y, radius / 4, width, height, level - 1);
            paintSnowLevel(g2d, x + radius, y, radius / 4, width, height, level - 1);
            paintSnowLevel(g2d, x + radius / 2, y - sin60, radius / 4, width, height, level - 1);
            paintSnowLevel(g2d, x + radius / 2, y + sin60, radius / 4, width, height, level - 1);
            paintSnowLevel(g2d, x - radius / 2, y - sin60, radius / 4, width, height, level - 1);
            paintSnowLevel(g2d, x - radius / 2, y + sin60, radius / 4, width, height, level - 1);

        } else {
            Path2D.Double path = new Path2D.Double();

            path.moveTo(x + width / 2, y + height / 2);
            path.lineTo(x + width / 2 + radius, y + height / 2);
            path.moveTo(x + width / 2, y + height / 2);
            path.lineTo(x + width / 2 - radius, y + height / 2);
            path.moveTo(x + width / 2, y + height / 2);
            path.lineTo(x + width / 2 + radius / 2, y + height / 2 - sin60);
            path.moveTo(x + width / 2, y + height / 2);
            path.lineTo(x + width / 2 - radius / 2, y + height / 2 - sin60);
            path.moveTo(x + width / 2, y + height / 2);
            path.lineTo(x + width / 2 + radius / 2, y + height / 2 + sin60);
            path.moveTo(x + width / 2, y + height / 2);
            path.lineTo(x + width / 2 - radius / 2, y + height / 2 + sin60);
            path.closePath();
            g2d.draw(path);
        }
    }


    public static void paintSnow(Graphics2D g2d, int width, int height, int radius, int levelCount) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color color = g2d.getColor();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(color);
        paintSnowLevel(g2d, 0, 0, radius, width, height, levelCount - 1);
    }
}
