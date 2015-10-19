/**
 * Created by yokoy on 10/18/15.
 */
import java.net.Socket;

public class ClientInfo {
    private String username;
    public Socket socket = null;
    private String address;
    private String port;

    public ClientInfo(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket(){
        return this.socket;
    }
}