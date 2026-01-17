import javax.swing.*;
import java.awt.event.*;
import java.rmi.server.UnicastRemoteObject;
import java.awt.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 50;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 150;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 2;
    int applesEaten = 0;
    int applesX;
    int applesY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random rand;
    String textFont = "Courier";
    Runnable onRestart;
    JButton restartButton;

    GamePanel(Runnable onRestart) {
        this.onRestart = onRestart;

        rand = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.green);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        setLayout(null);

        restartButton = new JButton("RESTART");
        restartButton.setFont(new Font(textFont, Font.BOLD, 30));
        restartButton.setBounds(200, 350, 200, 50);
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> onRestart.run());

        add(restartButton);
    }

    public void startGame() {
        restartButton.setVisible(false);

        bodyParts = 2;
        applesEaten = 0;
        direction = 'R';
        running = true;

        // First segment in the middle of the map
        x[0] = (SCREEN_WIDTH / UNIT_SIZE / 2) * UNIT_SIZE;
        y[0] = (SCREEN_HEIGHT / UNIT_SIZE / 2) * UNIT_SIZE;

        // Second segment behind the head
        x[1] = x[0] - UNIT_SIZE;
        y[1] = y[0];

        newApple();
        running = true;

        // Timer config
        if (timer != null)
            timer.stop();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (running) {
            for(int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++){
                for(int j = 0; j < SCREEN_WIDTH / UNIT_SIZE; j++){
                    if ((i + j) % 2 == 0) {
                        g2.setColor(new Color(18, 179, 21));
                    } else {
                        g2.setColor(new Color(11, 230, 16));
                    }
                    
                    g2.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                }
            }

            // Draw apples
            g2.setColor(Color.red);
            g2.fillOval(applesX, applesY, UNIT_SIZE, UNIT_SIZE);

            g2.setColor(new Color(255, 255, 255, 140));
            g2.fillOval(applesX + UNIT_SIZE/5, applesY + UNIT_SIZE/5, UNIT_SIZE/4, UNIT_SIZE/4);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g2.setColor(new Color(148, 81, 37));
                    switch (direction) {
                        case 'R' -> {
                            g2.fillRect(x[0], y[0], UNIT_SIZE / 2 + 1, UNIT_SIZE);                 // neck
                            g2.fillArc(x[0], y[0], UNIT_SIZE, UNIT_SIZE, -90, 180);                // nose
                        }
                        case 'L' -> {
                            g2.fillRect(x[0] + UNIT_SIZE / 2 - 1, y[0], UNIT_SIZE / 2 + 1, UNIT_SIZE);     // neck
                            g2.fillArc(x[0], y[0], UNIT_SIZE, UNIT_SIZE, 90, 180);                 // nose
                        }
                        case 'U' -> {
                            g2.fillRect(x[0], y[0] + UNIT_SIZE / 2 - 1, UNIT_SIZE, UNIT_SIZE / 2 + 1);     // neck
                            g2.fillArc(x[0], y[0], UNIT_SIZE, UNIT_SIZE, 0, 180);                  // nose
                        }
                        case 'D' -> {
                            g2.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE / 2 + 1);                 // neck
                            g2.fillArc(x[0], y[0], UNIT_SIZE, UNIT_SIZE, 180, 180);                // nose
                        }
                    }
                } else {
                    g2.setColor(new Color(161, 50, 50));
                    //g.setColor(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
                    g2.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g2.setColor(Color.WHITE);
            g2.setFont(new Font(textFont, Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g2.drawString("SCORE: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("SCORE: " + applesEaten))/2, g.getFont().getSize());
        }
        else {
            gameOver(g2);
        }
    }

    public void newApple() {
        while (true) {
            applesX = rand.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            applesY = rand.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

            boolean onSnake = false;
            for (int i = 0; i < bodyParts; i++) {
                if (x[i] == applesX && y[i] == applesY) {
                    onSnake = true;
                    break;
                }
            }

            if (!onSnake) {
                break;
            }
        }
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if (x[0] == applesX && y[0] == applesY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // checks if head collides with the body
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }
        // checks if head collides with left wall
        if (x[0] < 0) {
            running = false;
        }
        // checks if head collides with right wall
        if (x[0] >= SCREEN_WIDTH) {
            running = false;
        }
        // checks if head collides with ceiling
        if (y[0] < 0) {
            running = false;
        }
        // checks if head collides with floor
        if (y[0] >= SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
            restartButton.setVisible(true);
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font(textFont, Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        g.setColor(Color.WHITE);
        g.setFont(new Font(textFont, Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("SCORE: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("SCORE: " + applesEaten)) / 2,
                g.getFont().getSize());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
            }
        }
    }
}
