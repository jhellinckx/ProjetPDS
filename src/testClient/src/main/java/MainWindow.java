import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static org.calorycounter.shared.Constants.network.*;

/**
 * Created by aurelien on 27/02/16.
 */
public class MainWindow extends JFrame implements Observer {
    private static final Color BACKGROUND = new Color(60,63,65);

    private JPanel contentPane;
    private JComboBox request;
    private JButton send;
    private JButton clear_area;
    private JTextArea request_messages;
    private JButton specific_request;
    private JButton resend_request;
    private JPanel button_panel;
    private JPanel request_panel;

    private RequestControler controler;

    public MainWindow(RequestControler control){
        initWindow();
        initPanel();
        initBoxItems();
        addClearListener();
        addSpecificListener();
        request_messages.setBackground(Color.LIGHT_GRAY);
        controler = control;
    }

    private void initWindow(){
        contentPane.setBackground(BACKGROUND);
        this.setSize(1024,720);
        this.setResizable(false);
        this.setContentPane(contentPane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

    }

    private void initPanel(){
        button_panel.setBackground(BACKGROUND);
        request_panel.setBackground(BACKGROUND);
    }

    private void addClearListener(){
        clear_area.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                request_messages.setText(null);
            }
        });
    }

    private void addSpecificListener(){
        specific_request.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                RequestDialog dialog = new RequestDialog();
                dialog.pack();
                dialog.setVisible(true);
            }
        });
    }

    private void initBoxItems(){
        request.addItem(null);
        ArrayList<String> request_names = RequestNameContainer.getRequestNames();
        for (String name : request_names){
            request.addItem(name);
        }
    }

    @Override
    public void update(String msg){
        request_messages.append(msg + "\n");
    }
}
