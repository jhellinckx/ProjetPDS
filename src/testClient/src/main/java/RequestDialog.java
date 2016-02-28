import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class RequestDialog extends JDialog {
    private static final String KEY_PREFIX = "key";
    private static final String VALUE_PREFIX = "value";
    private static final int NUMBER_DATA_FIELDS = 8;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox request;
    private JPanel keys;
    private JPanel values;
    private JSplitPane divider_pane;
    private HashMap<Integer, JComboBox> map_keys;
    private HashMap<Integer, JTextField> map_values;
    private RequestDialogSendListener listener;

    public RequestDialog(MainWindow window) {
        setResizable(false);
        setContentPane(contentPane);
        contentPane.setBackground(new Color(60,63,65));
        initUIComponents();
        listener = window;
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public interface RequestDialogSendListener{
        public void onDialogSendAction(String name, List<String> keys, List<String> values);
    }

    private JComboBox initBox(List<String> key_s) {
        JComboBox box = new JComboBox();
        box.addItem(null);
        for (String key : key_s){
            box.addItem(key);
        }
        return box;

    }

    private void initKeys(){
        List<String> key_s = RequestKeysContainer.getKeys();
        for (int i = 0; i < NUMBER_DATA_FIELDS; i++) {
            JComboBox box = initBox(key_s);
            keys.add(box);
            map_keys.put(i, box);
        }
    }

    private void initValues(){
        for (int i = 0; i < NUMBER_DATA_FIELDS; i++) {
            JTextField text = new JTextField();
            values.add(text);
            map_values.put(i, text);
        }
    }

    private void initDataMap(){
        map_keys = new HashMap<>();
        map_values = new HashMap<>();
        initKeys();
        initValues();

    }

    private void initFieldPanel(JPanel pan){
        pan.setBackground(new Color(80,85,85));
        pan.setLayout(new GridLayout(NUMBER_DATA_FIELDS, 1));

    }

    private void initPanel(){
        initFieldPanel(keys);
        initFieldPanel(values);
        divider_pane.setBackground(new Color(80,85,85));

    }

    private void initBoxItems(){
        request.addItem(null);
        ArrayList<String> request_names = RequestNameContainer.getRequestNames();
        for (String name : request_names){
            request.addItem(name);
        }
    }

    private void initUIComponents(){
        initPanel();
        initDataMap();
        initBoxItems();
        divider_pane.setDividerLocation(288);           // TODO Replace this by a (getWidth()-margin-dividersize)/2.

    }

    private String getRequestName(){
        return (String) request.getSelectedItem();
    }

    private List<String> getDataFromMapValues(){
        ArrayList<String> data = new ArrayList<>();
        JTextField field;
        String value;
        for (int i = 0; i < NUMBER_DATA_FIELDS; i++){
            field = map_values.get(i);
            value = field.getText();
            if(!value.isEmpty()){
                data.add(value);
            }
        }
        return data;
    }

    private List<String> getDataFromMapKeys(){
        ArrayList<String> data = new ArrayList<>();
        JComboBox box;
        String key;
        for (int i = 0; i < NUMBER_DATA_FIELDS; i++){
            box = map_keys.get(i);
            key = (String) box.getSelectedItem();
            if (key != null){
                data.add(key);
            }
        }
        return data;

    }

    private List<String> getValues(){
        return getDataFromMapValues();
    }

    private List<String> getKeys(){
        return getDataFromMapKeys();
    }

    private void clearFields(){
        request.setSelectedIndex(0);
        for (int i = 0; i < NUMBER_DATA_FIELDS; i++){
            map_keys.get(i).setSelectedIndex(0);
            map_values.get(i).setText(null);
        }
    }

    private boolean dataAreCorrect(String name, List<String> key_s, List<String> value_s){
        if (name == null){
            return false;
        }
        if (key_s.isEmpty()){
            return false;
        }
        if (value_s.isEmpty()){
            return false;
        }
        if (key_s.size() != value_s.size()){
            return false;
        }
        return true;
    }

    private void onOK() {
        String request_name = getRequestName();
        List<String> key_s = getKeys();
        List<String> value_s = getValues();
        clearFields();
        if (dataAreCorrect(request_name, key_s, value_s)) {
            listener.onDialogSendAction(request_name, key_s, value_s);
        }
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }
}
