package ui.windows;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Created by aurelien on 12/03/16.
 */
public class MessageWindow extends JFrame {

    private static final String HEADER = "MESSAGE DU SERVEUR : ";
    private static final String BACKGROUND = "server/src/main/java/ui/background.jpg";
    private JLabel message;

    public MessageWindow(){
        this.setSize(750,500);
        this.setLayout(new BorderLayout());
        addBackground();
        initPosition();
        initUiComponents();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addBackground(){
        this.setContentPane(new JPanelWithBackground());
    }

    private void initPosition(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        this.setLocation((int)( (rect.getMaxX()-this.getWidth())/2),(int) (rect.getMaxY()-this.getHeight()));
    }

    private JLabel constructLabel(String default_text, Font font, Color color){
        JLabel label = new JLabel();
        if (default_text != null){
            label = new JLabel(default_text);
        }
        label.setForeground(color);
        label.setFont(font);
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    private void initUiComponents(){
        JLabel label = constructLabel(HEADER, new Font("Arial", Font.PLAIN, 40), Color.DARK_GRAY);
        this.getContentPane().add(label, BorderLayout.NORTH);
        message = constructLabel(null, new Font("Times", Font.PLAIN, 40), Color.BLACK);
        this.getContentPane().add(message, BorderLayout.CENTER);
    }

    public void setMessage(String message){
        this.message.setText(message);
        this.setVisible(true);
        try{
            Thread.sleep(3000);
        } catch (InterruptedException e){

        }
        this.dispose();
    }

    private class JPanelWithBackground extends JPanel{
        private Image background;

        public JPanelWithBackground(){
            try {
                background = ImageIO.read(new File(BACKGROUND));
            } catch (IOException e ){
                System.err.println(e.getMessage());
            }
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(background,0,0,this);
        }
    }
}
