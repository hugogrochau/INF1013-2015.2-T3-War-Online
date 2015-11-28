package org.puc.rio.war.server;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ClientThread implements Runnable {
	private Server server;
	private ObjectInputStream inputStream;
	private int id;

	public ClientThread(int id, Server server, ObjectInputStream inputStream) {
		this.id = id;
		this.server = server;
		this.inputStream = inputStream;
	}

	@Override
	public void run() {

		while (true) {
			String msg = null;
			try {
				msg = (String) inputStream.readObject();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			server.sendMessage(this.id, msg);
		}
	}
}
