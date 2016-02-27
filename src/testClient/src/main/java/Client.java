import org.calorycounter.shared.Constants;

public class Client{
    public static final void main(String[] args){
        ClientModel model = new ClientModel();
        RequestControler controler = new RequestControler(model);
        MainWindow window = new MainWindow(controler);
        window.setVisible(true);
    }
}