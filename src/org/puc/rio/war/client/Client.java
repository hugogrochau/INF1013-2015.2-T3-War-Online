package org.puc.rio.war.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import org.puc.rio.war.model.Message;

public class Client {
	private Socket server;
	
	public Client(String IP) throws UnknownHostException, IOException {
		server = new Socket(IP, 12345);

		MessageReceiver msgReceiver = new MessageReceiver(server.getInputStream());
		Thread receiverThread = new Thread(msgReceiver);
		receiverThread.start();
	}
	
	public void sendMessage(Message message) {
		ObjectOutputStream serverStream = null;
		try {
			serverStream = new ObjectOutputStream(server.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			serverStream.writeObject(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void closeConnection() {
		try {
			server.getInputStream().close();
			server.getOutputStream().close();
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
