/**
 * Created by yokoy on 10/18/15.
 */
import java.util.List;

public class ClientLists {
    public static List<ClientInfo> clientList;
    public int NumberOfClientsInChat;

    public void addClient(ClientInfo client){
        clientList.add(client);
    }
    public int getNumberOfClientsInChat(){
        return this.NumberOfClientsInChat;
    }
    public void setNumberOfClientsInChat(int num){
        this.NumberOfClientsInChat = num;
    }
}
