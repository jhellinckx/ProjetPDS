package ui.windows;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by aurelien on 12/03/16.
 */
public class LotteryWindow extends JFrame {
    private static final String BUTTON_TEXT = "Choisir un vainqueur";

    private JButton button;
    private WinningPanel winning_text;
    private WinnerListener listener;

    public LotteryWindow(WinnerListener listener){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.listener = listener;
        winning_text = new WinningPanel();
        initButton();
    }

    public interface WinnerListener{
        public void onWinnerRequest();
    }

    private void initButton(){
        button = new JButton(BUTTON_TEXT);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                listener.onWinnerRequest();
            }
        });
        addButton();
    }

    private void clearPanelComponent(){
        this.getContentPane().removeAll();
    }

    private void addButton(){
        this.getContentPane().add(button, BorderLayout.CENTER);
        this.pack();
    }

    private void centerWindow(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        this.setLocation((int) (rect.getMaxX()-this.getWidth())/2, (int) (rect.getMaxY()-this.getHeight())/2);
    }

    private void addWinningPanel(){
        this.getContentPane().add(winning_text, BorderLayout.CENTER);
        this.pack();
        centerWindow();
    }

    private void resetWindow(){
        clearPanelComponent();
        addButton();
    }

    public void displayWinner(String winner){
        winning_text.setWinnerName(winner);
        clearPanelComponent();
        addWinningPanel();
        try{
            Thread.sleep(10000);

        } catch (InterruptedException e){
            System.err.println(e.getMessage());
        }
        resetWindow();
    }

    private class WinningPanel extends JPanel{
        private static final String BACKGROUND = "server/src/main/java/ui/winning_screen.png";

        private JLabel winner_name;
        private Image background;

        public WinningPanel() {
            try {
                background = ImageIO.read(new File(BACKGROUND));
                this.setPreferredSize(new Dimension(background.getWidth(this), background.getHeight(this)));
            } catch (IOException e ){
                System.err.println(e.getMessage());
            }
            this.setLayout(new BorderLayout());
            initLabel();
        }

        private void initLabel(){
            winner_name = new JLabel();
            winner_name.setFont(new Font("Arial", Font.BOLD, 100));
            winner_name.setForeground(Color.BLACK);
            winner_name.setHorizontalAlignment(JLabel.CENTER);
            winner_name.setVerticalAlignment(JLabel.CENTER);
            this.add(winner_name, BorderLayout.CENTER);
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(background,0,0,this);
        }

        public void setWinnerName(String name){
            if (name == null){
                winner_name.setText("No winner ...");
            }
            else{
                winner_name.setText(name);
            }
        }
    }

}
