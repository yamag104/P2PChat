/**
 * ClientListener.java
 *
 * This class runs on the client end and just
 * displays any text received from the server.
 *
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
	private int turn = 0;
	private boolean neverOpened = true;

	ClientListener(Socket sock)
	{
		this.connectionSock = sock;
		socketList = new ArrayList<Socket>();
	}

	public void run()
	{
       		 // Wait for data from the server.  If received, output it.
		try
		{
			BufferedReader serverInput = new BufferedReader(new InputStreamReader(connectionSock.getInputStream()));
			String serverText = serverInput.readLine();
			if (serverText.equalsIgnoreCase("***1")) {
				//System.out.println("***1 received. First to connect!");
				turn = 1;
				neverOpened = true;
			} else if (serverText.equalsIgnoreCase("***2")) {
				//System.out.println("***2 received.");
				turn = 2;
			}
			while (true)
			{
				// Get data sent from the server
				serverText = serverInput.readLine();
				if (serverInput != null)
				{
					System.out.println(serverText);
				}
				else
				{
					// Connection was lost
					System.out.println("Closing connection for socket " + connectionSock);
					connectionSock.close();
					break;
				}
				// Establish server A
				if (turn == 1 && neverOpened == true) {
					neverOpened = false;
					openServerA();
				}
				else {
					openClientB();
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
	}
	public void openServerA() {
		// Wait for a connection from the client
		try
		{
			DataOutputStream serverOutput = new DataOutputStream(connectionSock.getOutputStream());
			//System.out.println("Waiting for client connections on port 9654.");
			serverOutput.writeBytes("Waiting for client connections on port 9654.\n");
			ServerSocket serverSock = new ServerSocket(9654);
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
			//System.out.println(e.getMessage());
		}
	}

	public void openClientB() {
		try
		{
			String hostname = "localhost";
			int port = 9654;

			//System.out.println("Connecting to server on port " + port);
			Socket connectionSock = new Socket(hostname, port);

			DataOutputStream serverOutput = new DataOutputStream(connectionSock.getOutputStream());
			serverOutput.writeBytes("Connecting to server on port " + port + "\n");
			serverOutput.writeBytes("Connection made.\n");
			//System.out.println("Connection made.");
			// Start a thread to listen and display data sent by the server
			ClientListener listener = new ClientListener(connectionSock);
			Thread theThread = new Thread(listener);
			theThread.start();

			// Read input from the keyboard and send it to everyone else.
			// The only way to quit is to hit control-c, but a quit command
			// could easily be added.
			Scanner keyboard = new Scanner(System.in);
			while (true)
			{
				String data = keyboard.nextLine();
				serverOutput.writeBytes(data + "\n");
			}
		}
		catch (IOException e)
		{
			//System.out.println(e.getMessage());
		}
	}
} // ClientListener for MTClient
