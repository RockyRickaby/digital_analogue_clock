import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.time.LocalDateTime;

public class AnalogueClock extends JFrame {
    private static final double ROTATION_ANGLE = Math.PI/30;
    private static final double HOUR_ANGLE = Math.PI/6;
    private static final double SMOOTH_ROTATION_ANGLE_MINUTE = Math.PI/1800;
    private static final double SMOOTH_ROTATION_ANGLE_HOUR = Math.PI/21600;

    private Point2D.Double secondsHand,
                           hoursHand,
                           minutesHand,
                           clockMiddle;

    private Timer timer;
    private int seconds;
    // to use with the updateClock method
    private int second, minute, hour;

    private double halfHeight;

    public AnalogueClock(boolean useCurrentSystemTime) {
        this.secondsHand = new Point2D.Double();
        this.hoursHand = new Point2D.Double();
        this.minutesHand = new Point2D.Double();
        this.clockMiddle = new Point2D.Double();

        this.halfHeight = -182;
        this.seconds = 43150;
        if (useCurrentSystemTime) {
            LocalDateTime currTime = LocalDateTime.now();
            seconds = currTime.getSecond() + 60*currTime.getMinute() + 3600*currTime.getHour();
        }
        
        this.second = seconds % 60;
        this.minute = seconds / 60 % 60;
        this.hour = seconds / 3600 % 24;

        this.timer = new Timer(1000, e -> {
            seconds = (seconds + 1) % 43200;

            second = seconds % 60;
            minute = seconds / 60 % 60;
            hour = seconds / 3600 % 24;
            updateClock();
            repaint();
        });

        this.setBackground(Color.WHITE);
        this.setTitle("Clock");
        this.add(generateMainPanel());
        // this.setJMenuBar(generateMenuBar());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(600, 623);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        // 584x584
        this.timer.start();
    }

    public void updateClock() {
        resetClock();
        //rotateHeads(second, minute, hour);
        rotateHeads(second, seconds, seconds);
    }

    public void resetClock() {
        secondsHand.x = 0;
        secondsHand.y = halfHeight;

        hoursHand.x = 0;
        hoursHand.y = 2*(halfHeight) / 3;

        minutesHand.x = 0;
        minutesHand.y = halfHeight - 15;
    }

    // how much to rotate the heads related to where they're currently pointing at.
    private void rotateHeads(int rotateSec, int rotateMin, int rotateHour) {
        double secondsAngle = rotateSec * ROTATION_ANGLE;
        // double minutesAngle = rotateMin * ROTATION_ANGLE;
        // double hoursAngle = rotateHour * HOUR_ANGLE;

        double minutesAngle = rotateMin*SMOOTH_ROTATION_ANGLE_MINUTE;
        double hoursAngle = rotateHour*SMOOTH_ROTATION_ANGLE_HOUR;

        double x, y;
        double sin, cos;

        // seconds hand
        sin = Math.sin(secondsAngle);
        cos = Math.cos(secondsAngle);

        x = secondsHand.x*cos - secondsHand.y*sin;
        y = secondsHand.x*sin + secondsHand.y*cos;

        secondsHand.x = x;
        secondsHand.y = y;

        // minutes hand
        sin = Math.sin(minutesAngle);
        cos = Math.cos(minutesAngle);

        x = minutesHand.x*cos - minutesHand.y*sin;
        y = minutesHand.x*sin + minutesHand.y*cos;

        minutesHand.x = x;
        minutesHand.y = y;

        // hours hand
        sin = Math.sin(hoursAngle);
        cos = Math.cos(hoursAngle);

        x = hoursHand.x*cos - hoursHand.y*sin;
        y = hoursHand.x*sin + hoursHand.y*cos;

        hoursHand.x = x;
        hoursHand.y = y;
    }

    public JPanel generateMainPanel() {
        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                double widthHalf = getWidth() / 2.;
                double heightHalf = getHeight() / 2.;

                g2.translate(widthHalf, heightHalf);

                Point2D.Double hourTick = new Point2D.Double(0, -heightHalf + 30);
                Point2D.Double hourTick2 = new Point2D.Double(0, -heightHalf + 40);

                g2.setColor(Color.BLACK);

                // clock's midpoint. just a reference point.
                g2.draw(new Ellipse2D.Double(-2, -2, 4, 4));

                g2.setStroke(new BasicStroke(6));
                g2.draw(new Line2D.Double(clockMiddle, hoursHand));

                g2.setStroke(new BasicStroke(4));
                g2.draw(new Line2D.Double(clockMiddle, minutesHand));

                g2.setColor(Color.RED);
                
                g2.setStroke(new BasicStroke(2));
                g2.draw(new Line2D.Double(clockMiddle, secondsHand)); // seconds hand
                
                BasicStroke quarterStroke = new BasicStroke(10),
                            hourStroke = new BasicStroke(6),
                            secondStroke = new BasicStroke(2);

                g2.setColor(Color.BLACK);
                g2.setStroke(quarterStroke);
                g2.draw(new Line2D.Double(hourTick, hourTick2));

                for (int i = 0; i < 60; i++) {
                    double angle = i*ROTATION_ANGLE;
                    if (i >= 15 && i % 15 == 0) {
                        g2.setStroke(quarterStroke);
                    } else if (i >= 5 && i % 5 == 0) {
                        g2.setStroke(hourStroke);
                    } else {
                        g2.setStroke(secondStroke);
                    }
                    double sin = Math.sin(angle);
                    double cos = Math.cos(angle);

                    double x1 = hourTick.x*cos - hourTick.y*sin;
                    double y1 = hourTick.x*sin + hourTick.y*cos;

                    double x2 = hourTick2.x*cos - hourTick2.y*sin;
                    double y2 = hourTick2.x*sin + hourTick2.y*cos;

                    g2.draw(new Line2D.Double(x1, y1, x2, y2));
                }
            }
        };
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                halfHeight = (double) -getHeight() / 2 + 110;
                resetClock();
                //rotateHeads(second, minute, hour);
                rotateHeads(second, seconds, seconds);
            }
        });
        return panel;
    }
}
