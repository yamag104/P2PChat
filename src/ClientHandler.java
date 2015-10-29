/**
 * ClientHandler.java
 *
 * This class handles communication between the client
 * and the server.  It runs in a separate thread but has a
 * link to a common list of sockets to handle broadcast.
 * @author Yoko Yamaguchi
 */
import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;

import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable
{
	private Socket connectionSock = null;
	private ArrayList<Socket> socketList;
	DataOutputStream Output = null;
	private boolean opened = true;
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
			BufferedReader clientInput = new BufferedReader(
				new InputStreamReader(connectionSock.getInputStream()));
            DataOutputStream Output = new DataOutputStream(connectionSock.getOutputStream());

			/* If this is the first server with port 7654, check the connecting order*/
			if (connectionSock.getLocalPort() == 7654) {
				System.out.println("SocketList Size:" + socketList.size());
				if (socketList.size() == 1) {
					System.out.println("Connection made with socket " + connectionSock);
					System.out.println("^First to connect to server. ***1 sent.");
					Output.writeBytes("***1" + "\n");
				} else if (socketList.size() > 1) {
					System.out.println("Connection made with socket " + connectionSock);
					//System.out.println("***2 sent.");
					Output.writeBytes("***2" + "\n");
				}
			}
				/* Printing out the connected clients */
//				if (socketList.size() == 1) {
////					Output.writeBytes("Noone is connected yet." + "\n");
//				} else {
//					for (Socket s : socketList)
//					{
//						if (s != connectionSock)
//						{
//							Output.writeBytes(s + "\n");
//
//						}
//					}
//				}
			if (connectionSock.getLocalPort() == 9654) {
				Socket mainServerSocket = new Socket("localhost",7654);
				DataOutputStream OutputToMainServer = new DataOutputStream(mainServerSocket.getOutputStream());
				OutputToMainServer.writeBytes("ServerA: Connection made with socket " + connectionSock + "\n");
			}

			while (true)
			{
				// Get data sent from a client
				String clientText = clientInput.readLine();
				if (clientText != null)
				{
					System.out.println("Received: " + clientText);
					/**
					 * If a client user sends a message containing the single letter "r" (for "refresh"),
					 * the message will be delivered to the central server and the client will receive a
					 * refreshed, updated, list of clients that have connected to the central server.
					 */
					if (clientText.equalsIgnoreCase("r")) {
						System.out.println("Refresh");
						int index = 0;
						for (Socket s : socketList)
						{
							if (s != connectionSock) {
								Output.writeBytes(index + ". " + s + "\n");
								index++;
							}
						}
						Output.writeBytes("Choose a client to connect to>\n");
						clientText = clientInput.readLine();
						if (clientText != null) {
							// Connect to client.
							int chosenClientIndex = Integer.parseInt(clientText);
							Output.writeBytes("Connecting to " + socketList.get(chosenClientIndex) + "......\n");
							Output.writeBytes("##" + socketList.get(chosenClientIndex) + "\n");
						}

					}
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
			System.out.println("+Error: " + e.toString());
			// Remove from arraylist
			socketList.remove(connectionSock);
		}
	}
} // ClientHandler for MTServer.java
