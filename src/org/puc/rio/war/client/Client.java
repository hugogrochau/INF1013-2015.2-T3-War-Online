package org.puc.rio.war.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.puc.rio.war.model.Message;

public class Client {
	private Socket server;
	private ObjectOutputStream serverStream = null;
	
	public Client(String IP) throws UnknownHostException, IOException {
		server = new Socket(IP, 12345);
		this.serverStream = new ObjectOutputStream(server.getOutputStream());

		MessageReceiver msgReceiver = new MessageReceiver(server.getInputStream());
		 new Thread(msgReceiver).start();
	}
	
	public void sendMessage(Message message) {
		try {
			this.serverStream.writeObject(message);
			this.serverStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			server.getInputStream().close();
			server.getOutputStream().close();
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
