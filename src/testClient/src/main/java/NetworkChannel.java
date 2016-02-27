import java.io.IOException;

public interface NetworkChannel {

    public void initiateConnection() throws IOException;
    public String read() throws IOException;
    public void write(String msg) throws IOException;
}
