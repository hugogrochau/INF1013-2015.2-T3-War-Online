package org.puc.rio.war.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import org.puc.rio.war.model.Message;

public class ClientThread extends Thread {
	private Server server;
	private Socket clientSocket;
	private ObjectInputStream inputStream;
	private int id;

	public ClientThread(int id, Server server, Socket socket) {
		this.id = id;
		this.server = server;
		this.clientSocket = socket;
		try {
			this.inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		while (true) {
			Message msg = null;
			try {
				msg = (Message) this.inputStream.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				server.closeSocket(this.clientSocket, this.id);
				break;
			}
			server.sendMessage(this.id, msg);
		}
	}
}
