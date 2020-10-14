package chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
/**
 * Brian Boltiansky
 */
public class ChatServer extends ChatWindow {
  //changed from 1 ClientHandler to an ArrayList to handle multiple users at one time
	private ArrayList<ClientHandler> allClients = new ArrayList<ClientHandler>();
	
  public ChatServer(){
		super();
		this.setTitle("Chat Server");
		this.setLocation(80,80);

		try {
			// Create a listening service for connections
			// at the designated port number.
			ServerSocket srv = new ServerSocket(5030);//correct port number

			while (true) {
				// The method accept() blocks until a client connects.
				printMsg("Waiting for a connection");
				Socket socket = srv.accept();
        			ClientHandler handler = new ClientHandler(socket);
				allClients.add(handler);	
        			handler.connect();
			}

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/** This innter class handles communication to/from one client. */
	class ClientHandler implements Runnable{
		private PrintWriter writer;
		private BufferedReader reader;

		public ClientHandler(Socket socket){
			try {
				InetAddress serverIP = socket.getInetAddress();
				printMsg("Connection made to " + serverIP);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch (IOException e){
					printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
				}
		}
		public void handleConnection() {
			try {
				while(true) {//Loop to read messages
          sendMsg(readMsg());
				}
			}
			catch (IOException e){
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}

		/** Receive and display a message */
		public String readMsg() throws IOException {
			String s = reader.readLine();
			printMsg(s);
     			return s;
		}
		/** Send a string */
		public void sendMsg(String s){
      for(ClientHandler client : allClients)//send message to all available users
        client.writer.println(s);
      
		}
    public void connect(){
      Thread trd = new Thread(this);//start the threading
      trd.start();
    }
    public void run(){
      handleConnection();//run method for threading
    }

	}

	public static void main(String args[]){
		new ChatServer();
	}
}
