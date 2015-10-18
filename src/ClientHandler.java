/**
 * ClientHandler.java
 *
 * This class handles communication between the client
 * and the server.  It runs in a separate thread but has a
 * link to a common list of sockets to handle broadcast.
 *
 */
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable
{
	private Socket connectionSock = null;
	private ArrayList<Socket> socketList;
	private HashMap<String, Integer> hmap = new HashMap<String, Integer>();

	ClientHandler(Socket sock, ArrayList<Socket> socketList)
	{
		this.connectionSock = sock;
		this.socketList = socketList;	// Keep reference to master list
	}

	public void run()
	{
        		// Get data from a client and send it to everyone else
		try
		{
			System.out.println("Connection made with socket " + connectionSock);
			String ipAddress = connectionSock.getInetAddress().getHostAddress();
			Integer port = connectionSock.getPort();
			hmap.put(ipAddress, port);
			BufferedReader clientInput = new BufferedReader(
				new InputStreamReader(connectionSock.getInputStream()));
			Iterator it = hmap.entrySet().iterator();
			if (!hmap.isEmpty()){
		    	while (it.hasNext()) {
		        	Map.Entry pair = (Map.Entry)it.next();
		        	String socketinfo = "[IP:"+ pair.getKey() + "Port:" + pair.getValue()+"]";
		        	System.out.println(socketinfo);
						for (Socket s : socketList)
						{
							if (s != connectionSock)
							{
								DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
								clientOutput.writeBytes(socketinfo + "\n");
							}
						}
		        	it.remove(); // avoids a ConcurrentModificationException
    			}
			}

			while (true)
			{
				// Get data sent from a client
				String clientText = clientInput.readLine();
				// if (clientText.startsWith("Conncetion made with socket"))
				// {
				// 	String ipAddress = connectionSock.getInetAddress().getHostAddress();
				// 	Integer port = connectionSock.getPort();
				// 	System.out.println("ip:" + ipAddress);
				// 	System.out.println("port" + port);
				// 	hmap.put(ipAddress, port);

				// }
				if (clientText != null)
				{
					System.out.println("Received: " + clientText);
					// Turn around and output this data
					// to all other clients except the one
					// that sent us this information
					for (Socket s : socketList)
					{
						if (s != connectionSock)
						{
							DataOutputStream clientOutput = new DataOutputStream(s.getOutputStream());
							clientOutput.writeBytes(clientText + "\n");

						}
					}
				}
				else
				{
				  // Connection was lost
				  System.out.println("Closing connection for socket " + connectionSock);
				   // Remove from arraylist
				   socketList.remove(connectionSock);
				   connectionSock.close();
				   break;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
			// Remove from arraylist
			socketList.remove(connectionSock);
		}
	}
} // ClientHandler for MTServer.java
