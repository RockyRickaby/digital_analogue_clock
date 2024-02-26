import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
    private boolean showDigitalClock, digitalClockIs24h, smoothRotationsEnabled;
    // to use with the updateClock method
    private int second, minute, hour;

    private double halfHeight;

    /**
     * Creates a new AnalogueClock. It may use the current system time
     * as a starting point or not. This is defined by the parameter
     * {@code useCurrentSystemTime}.
     * 
     * @param useCurrentSystemTime whether this clock will use the current
     * system time or not.
     */
    public AnalogueClock(boolean useCurrentSystemTime) {
        this.secondsHand = new Point2D.Double();
        this.hoursHand = new Point2D.Double();
        this.minutesHand = new Point2D.Double();
        this.clockMiddle = new Point2D.Double();
        
        this.halfHeight = -182;
        this.seconds = 0;
        this.showDigitalClock = false;
        this.digitalClockIs24h = false;
        this.smoothRotationsEnabled = true;
        
        if (useCurrentSystemTime) {
            LocalDateTime currTime = LocalDateTime.now();
            seconds = currTime.getSecond() + 60*currTime.getMinute() + 3600*currTime.getHour();
        }
        
        this.second = seconds % 60;
        this.minute = seconds / 60 % 60;
        this.hour = seconds / 3600 % 24;

        this.timer = new Timer(1000, e -> {
            seconds = (seconds + 1) % 86400;

            second = seconds % 60;
            minute = seconds / 60 % 60;
            hour = seconds / 3600 % 24;
            updateClock();
            repaint();
        });

        this.setBackground(Color.WHITE);
        this.setTitle("Clock");
        this.add(generateMainPanel());
        this.setJMenuBar(generateMenuBar());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(600, 646);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        // 584x584
        this.timer.start();
    }

    /**
     * Generates this Frame's menu bar.
     * @return the menu bar.
     */
    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem item = new JMenuItem("Toggle digital clock");
        item.addActionListener(e-> {
            this.showDigitalClock = !this.showDigitalClock;
            repaint();
        });
        menu.add(item);
        
        item = new JMenuItem("Toggle 24 hour clock");
        item.addActionListener(e-> {
            if (!this.showDigitalClock) {
                return;
            }
            this.digitalClockIs24h = !this.digitalClockIs24h;
            repaint();
        });
        menu.add(item);
        
        item = new JMenuItem("Toggle smooth head rotations");
        item.addActionListener(e-> {
            this.smoothRotationsEnabled = !this.smoothRotationsEnabled;
            updateClock();
            repaint();
        });
        menu.add(item);
        menu.addSeparator();

        item = new JMenuItem("Exit");
        item.addActionListener(e -> System.exit(0));
        menu.add(item);

        menuBar.add(menu);
        return menuBar;
    }

    /**
     * Updates the clock, rotating its heads so they point to the
     * correct parts of the clock, indicating the correct time.
     * The result depends on the number of seconds
     * passed since the initialization of this AnalogueClock.
     * <p>
     * The starting
     * state of the clock may vary depending on whether this AnalogueClock
     * uses the current system time or not.
     */
    public void updateClock() {
        resetClock();
        double secondsAngle = second * ROTATION_ANGLE;
        double minutesAngle = minute * ROTATION_ANGLE;
        double hoursAngle = hour * HOUR_ANGLE;

        if (smoothRotationsEnabled) {
            minutesAngle = seconds * SMOOTH_ROTATION_ANGLE_MINUTE;
            hoursAngle = seconds * SMOOTH_ROTATION_ANGLE_HOUR;
        }

        rotateHeads(secondsAngle, minutesAngle, hoursAngle);
    }

    /**
     * Resets the heads positions, making them all point at 12 o'clock.
     */
    public void resetClock() {
        secondsHand.x = 0;
        secondsHand.y = halfHeight;

        hoursHand.x = 0;
        hoursHand.y = 2*(halfHeight) / 3;

        minutesHand.x = 0;
        minutesHand.y = halfHeight - 15;
    }

    /**
     * Rotates the heads through their respective angles (in radians) in relation to
     * their current position on the clock.
     * 
     * @param secondsHeadAngle angle through which the second head will be rotated.
     * @param minutesHeadAngle angle through which the minute head will be rotated.
     * @param hoursHeadAngle angle through which the hour head will be rotated.
     */
    private void rotateHeads(double secondsHeadAngle, double minutesHeadAngle, double hoursHeadAngle) {
        double x, y;
        double sin, cos;

        // seconds hand
        sin = Math.sin(secondsHeadAngle);
        cos = Math.cos(secondsHeadAngle);

        x = secondsHand.x*cos - secondsHand.y*sin;
        y = secondsHand.x*sin + secondsHand.y*cos;

        secondsHand.x = x;
        secondsHand.y = y;

        // minutes hand
        sin = Math.sin(minutesHeadAngle);
        cos = Math.cos(minutesHeadAngle);

        x = minutesHand.x*cos - minutesHand.y*sin;
        y = minutesHand.x*sin + minutesHand.y*cos;

        minutesHand.x = x;
        minutesHand.y = y;

        // hours hand
        sin = Math.sin(hoursHeadAngle);
        cos = Math.cos(hoursHeadAngle);

        x = hoursHand.x*cos - hoursHand.y*sin;
        y = hoursHand.x*sin + hoursHand.y*cos;

        hoursHand.x = x;
        hoursHand.y = y;
    }

    /**
     * Generates the this frame's main JPanel and defined how the clock
     * should be rendered on screen.
     * @return the main JPanel.
     */
    private JPanel generateMainPanel() {
        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                double widthHalf = getWidth() / 2.;
                double heightHalf = getHeight() / 2.;

                g2.translate(widthHalf, heightHalf);

                if (showDigitalClock) {
                    g2.setFont(new Font("Arial", Font.BOLD, 32));
                    int x = -40, y = 76;
                    if (!digitalClockIs24h) {
                        x = -64;
                    }
                    g2.drawString(DigitalClockString.toString(hour, minute, digitalClockIs24h), x, y);
                }

                Point2D.Double hourTick = new Point2D.Double(0, -heightHalf + 30);
                Point2D.Double hourTick2 = new Point2D.Double(0, -heightHalf + 40);

                g2.setColor(Color.BLACK);

                // clock's midpoint. just a reference point.
                // g2.draw(new Ellipse2D.Double(-2, -2, 4, 4));

                g2.setStroke(new BasicStroke(7));
                g2.draw(new Line2D.Double(clockMiddle, hoursHand));

                g2.setStroke(new BasicStroke(5));
                g2.draw(new Line2D.Double(clockMiddle, minutesHand));

                g2.setColor(Color.RED);
                
                g2.setStroke(new BasicStroke(2));
                g2.draw(new Line2D.Double(clockMiddle, secondsHand)); // seconds hand
                
                BasicStroke quarterStroke = new BasicStroke(10),
                            hourStroke = new BasicStroke(6),
                            secondStroke = new BasicStroke(2);

                g2.setColor(Color.BLACK);
                g2.setStroke(quarterStroke);

                for (int i = 0; i <= 60; i++) {
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
                updateClock();
            }
        });
        return panel;
    }
}
