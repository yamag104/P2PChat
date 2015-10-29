import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by yokoy on 10/28/15.
 */
public class PeerServer {
    // Maintain list of all client sockets for broadcast
    private ArrayList<Socket> socketList;

    public PeerServer()
    {
        socketList = new ArrayList<Socket>();
    }

    private void getConnection(int port)
    {
        // Wait for a connection from the client
        try
        {
            System.out.println("Waiting for client connections on port 7654.");
            ServerSocket serverSock = new ServerSocket(port);
            // This is an infinite loop, the user will have to shut it down
            // using control-c
            while (true)
            {
                Socket connectionSock = serverSock.accept();
                // Add this socket to the list
                socketList.add(connectionSock);
                // Send to ClientHandler the socket and arraylist of all sockets
                ClientHandler handler = new ClientHandler(connectionSock, this.socketList);
                Thread theThread = new Thread(handler);
                theThread.start();
            }
            // Will never get here, but if the above loop is given
            // an exit condition then we'll go ahead and close the socket
            //serverSock.close();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static void main(int port)
    {
        PeerServer server = new PeerServer();
        server.getConnection(port);
    }
}
