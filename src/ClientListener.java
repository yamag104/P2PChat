/**
 * ClientListener.java
 *
 * This class runs on the client end and just
 * displays any text received from the server.
 *
 *  @author Yoko Yamaguchi
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class ClientListener implements Runnable
{
	private Socket connectionSock = null;
	private ArrayList<Socket> socketList;
	private boolean serverNeverOpened = false;
	private boolean newClient = false;
	int port = 7655;

	ClientListener(Socket sock)
	{
		this.connectionSock = sock;
		socketList = new ArrayList<Socket>();
	}

	public void run()
	{
       		 // Wait for data from the server.  If received, output it.
//		int turn =0;
		int port = 7655;
		try
		{
			BufferedReader serverInput = new BufferedReader(new InputStreamReader(connectionSock.getInputStream()));
			if (serverInput != null) {
				String serverText = serverInput.readLine();

				//disabled for assignment-3
//				if (serverText.equalsIgnoreCase("***1")) {
//					System.out.println("***1 received. First to connect!");
//					turn = 1;
//					serverNeverOpened = true;
//				} else if (serverText.equalsIgnoreCase("***2")) {
//					System.out.println("***2 received.");
//					newClient = true;
//					turn = 2;
//					openClient(9654);
//				}
				while (true)
				{
//					if (turn == 1 && serverNeverOpened == true) {
//						openClientSpecificServer(9654);
//						serverNeverOpened = false;
//					} else if (turn == 2 && newClient == true) {
////						openClient(9654);
//						newClient = false;
//					}
					serverText = serverInput.readLine();
					if (serverInput != null)
					{
						if (serverText.startsWith("##")) {
							System.out.println("## Received");
							openClientSpecificServer(port);
							port++;

						} else {
							System.out.println(serverText);
						}
					}
					else
					{
						// Connection was lost
						System.out.println("Closing connection for socket " + connectionSock);
						connectionSock.close();
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("*Error: " + e.toString());
		}
	}
	public void openServerA() {
		// Wait for a connection from the client
		try
		{
			DataOutputStream serverOutput = new DataOutputStream(connectionSock.getOutputStream());
			serverOutput.writeBytes("ServerA: *-*-*- Server A opened -*-*-*\n");
			serverOutput.writeBytes("ServerA: Waiting for client connections on port 9654.\n");
			ServerSocket serverSock = new ServerSocket(9654);
			// This is an infinite loop, the user will have to shut it down
			// using control-c
			while (true)
			{
				Socket connectionSock = serverSock.accept();
				/* Goes here when Client B Connects */
				// Add this socket to the list
				socketList.add(connectionSock);
				// Send to ClientHandler the socket and arraylist of all sockets
				ClientHandler handler = new ClientHandler(connectionSock, this.socketList);
				Thread theThreadA = new Thread(handler);
				theThreadA.start();
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

	public void openClient(int port) {
		try
		{
			String hostname = "localhost";

			Socket connectionSock = new Socket(hostname, port);

			DataOutputStream serverOutput = new DataOutputStream(connectionSock.getOutputStream());
			System.out.println("Connecting to server on port " + port);
			// Start a thread to listen and display data sent by the server
			ClientListener listener = new ClientListener(connectionSock);
			Thread clientThread = new Thread(listener);
			clientThread.start();

			// Read input from the keyboard and send it to everyone else.
			// The only way to quit is to hit control-c, but a quit command
			// could easily be added.
			serverOutput.writeBytes(connectionSock + "Connecting to server on port " + port + "\n");
			System.out.println("Say Hi!");

			Scanner keyboard = new Scanner(System.in); //something wrong here
			while (true)
			{
				String data = keyboard.nextLine();
				serverOutput.writeBytes(data + "\n");
			}
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public void openClientSpecificServer(int port) {
		// Wait for a connection from the client
		try
		{
			DataOutputStream serverOutput = new DataOutputStream(connectionSock.getOutputStream());
			serverOutput.writeBytes("Server: *-*-*- Server opened -*-*-*\n");
			serverOutput.writeBytes("Server: Waiting for client connections on port " + port +"\n");
			ServerSocket serverSock = new ServerSocket(port);
			// This is an infinite loop, the user will have to shut it down
			// using control-c
			while (true)
			{
				Socket connectionSock = serverSock.accept();
				/* Goes here when Client B Connects */
				// Add this socket to the list
				socketList.add(connectionSock);
				// Send to ClientHandler the socket and arraylist of all sockets
				ClientHandler handler = new ClientHandler(connectionSock, this.socketList);
				Thread theThreadA = new Thread(handler);
				theThreadA.start();
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

	public void openWelcomeSocket() {
		PeerServer peerServer = new PeerServer();
		peerServer.main(port++);
	}
} // ClientListener for MTClient
