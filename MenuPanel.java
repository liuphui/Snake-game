import java.awt.*;
import javax.swing.*;

public class MenuPanel extends JPanel {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    String textFont = "Courier";

    public MenuPanel(Runnable onStart){
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLUE);
        setLayout(new GridBagLayout());

        JLabel title = new JLabel(
        "<html><center>Welcome to the Game!<br>Survive as long as possible</center></html>"
        );
        title.setFont(new Font(textFont, Font.BOLD, 40));
        title.setForeground(Color.WHITE);

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font(textFont, Font.BOLD, 25));
        startButton.addActionListener(e -> onStart.run());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 0, 0);
        
        add(startButton, gbc);
    }
}
