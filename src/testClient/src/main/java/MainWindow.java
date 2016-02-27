import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.calorycounter.shared.Constants.network.*;

/**
 * Created by aurelien on 27/02/16.
 */
public class MainWindow extends JFrame implements Observer, RequestDialog.RequestDialogSendListener {
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
    private JButton log_user;

    private RequestControler controler;
    private RequestFactory request_factory;
    private RequestDialog request_dialog;

    public MainWindow(RequestControler control){
        initWindow();
        initPanel();
        initBoxItems();
        addListeners();
        request_dialog = new RequestDialog(this);
        request_messages.setBackground(Color.LIGHT_GRAY);
        controler = control;
        request_factory = new RequestFactory();
    }

    private void initWindow(){
        contentPane.setBackground(BACKGROUND);
        this.setSize(1024,720);
        this.setResizable(false);
        this.setContentPane(contentPane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

    }

    private void addListeners(){
        addClearListener();
        addSpecificListener();
        addSendListener();
        addResendListener();
        addLogListener();
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
                request_dialog.pack();
                request_dialog.setVisible(true);
            }
        });
    }

    private void addSendListener(){
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String item = (String) request.getSelectedItem();
                if (item != null){
                    Request request = request_factory.getRequestInstance(item, new ArrayList<String>(), new ArrayList<String>());
                    controler.sendRequest(request);
                    controler.getResponse();
                }
            }
        });
    }

    private void addResendListener(){
        resend_request.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                controler.sendLastRequest();
            }
        });
    }

    private void addLogListener(){
        log_user.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ArrayList<String> keys = new ArrayList<String>(Arrays.asList(USERNAME, PASSWORD));
                ArrayList<String> values = new ArrayList<String>(Arrays.asList("a", "a"));
                Request request = request_factory.getRequestInstance(LOG_IN_REQUEST, keys, values);
                controler.sendRequest(request);
                controler.getResponse();
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
    public void onDialogSendAction(String name, List<String> keys, List<String> values){
        Request request = request_factory.getRequestInstance(name, keys, values);
        controler.sendRequest(request);
        controler.getResponse();

    }

    @Override
    public void update(String msg){
        request_messages.append(msg + "\n");
    }
}
