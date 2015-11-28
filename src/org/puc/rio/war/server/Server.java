package org.puc.rio.war.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private ArrayList<ObjectOutputStream> clientStreams = new ArrayList<ObjectOutputStream>();
	private int clientCount = 0;
	private ServerSocket server = null;
	public Server() {
		try {
			this.server = new ServerSocket(12345);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			Socket client = null;
			try {
				client = server.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("New connection with: " + client.getInetAddress().getHostAddress());
			try {
				ClientThread clientThread = new ClientThread(clientCount, this, new ObjectInputStream(client.getInputStream()));
				clientStreams.add(clientCount, new ObjectOutputStream(client.getOutputStream()));
				new Thread(clientThread).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void closeSocket(Socket client, ObjectOutputStream stream) throws IOException {
		clientStreams.remove(client);
		client.close();
	}

	public void sendMessage(int id, String message) {
		System.out.println("Sending message:");
		System.out.println(message);
		for (int i = 0; i < clientStreams.size(); i++) {
			if (i != id) {
				ObjectOutputStream stream = clientStreams.get(i);
				try {
					stream.writeObject(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void close() {
		try {
			this.server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
