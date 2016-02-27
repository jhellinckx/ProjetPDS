import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

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
    private HashMap<Integer, JTextField> map_keys;
    private HashMap<Integer, JTextField> map_values;

    public RequestDialog() {
        setResizable(false);
        setContentPane(contentPane);
        contentPane.setBackground(new Color(60,63,65));
        initUIComponents();
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

    private void initMap(HashMap<Integer,JTextField> map, JPanel pan ) {
        for (int i = 0; i < NUMBER_DATA_FIELDS; i++) {
            JTextField text = new JTextField();
            pan.add(text);
            map.put(i, text);
        }

    }

    private void initDataMap(){
        map_keys = new HashMap<Integer, JTextField>();
        map_values = new HashMap<Integer, JTextField>();
        initMap(map_keys, keys);
        initMap(map_values, values);

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

    private void initUIComponents(){
        initPanel();
        initDataMap();
        divider_pane.setDividerLocation(288);           // TODO Replace this by a (getWidth()-margin-dividersize)/2.

    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }
}
