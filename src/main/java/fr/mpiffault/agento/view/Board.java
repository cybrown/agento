package fr.mpiffault.agento.view;

import fr.mpiffault.agento.model.Agent;
import fr.mpiffault.agento.model.Environment;
import fr.mpiffault.agento.model.Position;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Board extends JPanel implements Runnable {

    private static final long serialVersionUID = -8954030686627371948L;
    public static long TIME_RESOLUTION = 30l;
    private final Environment environment;
    @Getter
    private int sizeX;
    @Getter
    private int sizeY;

    Board(final Environment environment) {
        super();
        setBackground(Color.WHITE);
        this.environment = environment;
        this.sizeX = (int) this.environment.getSizeX();
        this.sizeY = (int) this.environment.getSizeY();

        Mouse s = new Mouse();
        this.addMouseListener(s);
		this.addMouseMotionListener(s);

    }

    private class Mouse extends MouseAdapter {
        Agent c;

        @Override
        public void mousePressed(MouseEvent e) {
            c = new Agent(environment, new Position(e.getX(), e.getY()));
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (c != null) {
                c.setPosition(new Position(e.getX(), e.getY()));
            }
        }
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g2);
		// drawGradient(g2);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawAgents(g2);
        g2.setColor(Color.RED);
        g2.drawRect(0, 0, this.sizeX, this.sizeY);
    }

    public void drawAgents(Graphics2D g2) {
        for (Drawable drawable : environment.getAgentList()) {
            drawable.draw(g2);
        }
    }

    public void drawGradient(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        for (int iX = 0 ; iX < sizeX ; iX++) {
            for (int iY = 0 ; iY < sizeY ; iY++) {
                double intensite = environment.getGravityIntensityAt(iX, iY);
                g2.setColor(intensiteToColor(intensite, 100));
                g2.drawLine(iX, iY, iX, iY);
            }
        }
    }

    /**
     * Return a color according to intensity given between 0(BLACK) et 1785(WHITE) and a max value
     * The color gradient sequence is Black=>Blue=>Cyan=>Green=>Yellow=>Red=>Pink=>White
     * @param intensityAsDouble intensity. a negative value will return BLACK
     * @param maxValue value beyond which it will be WHITE
     * @return A Color between BLACK (0) and WHITE (maxValue)
     */
    public Color intensiteToColor(double intensityAsDouble, double maxValue) {
        intensityAsDouble = (intensityAsDouble / maxValue) * 1785;
        int intensite = (int) intensityAsDouble;
        if (intensite >= 1785) return Color.WHITE;
        if (intensite <= 0) return Color.BLACK;
        if (intensite < 255)
            return new Color(0,0,intensite);
        else if (intensite < 510)
            return new Color(0, intensite%255, 255);
        else if (intensite < 765)
            return new Color(0, 255, 255 - intensite%255);
        else if (intensite < 1020)
            return new Color(intensite%255, 255, 0);
        else if (intensite < 1275)
            return new Color(255, 255 - intensite%255, 0);
        else if (intensite < 1530)
            return new Color(255, 0, intensite%255);
        else if (intensite < 1785)
            return new Color(255, intensite%255, 255);
        return Color.WHITE;

    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(TIME_RESOLUTION);
                environment.tick();
                this.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
