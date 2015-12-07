package org.puc.rio.war.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.puc.rio.war.model.Message;

public class Server {
	private ArrayList<ObjectOutputStream> clientStreams = new ArrayList<ObjectOutputStream>();
	private int clientCount = 0;
	private ServerSocket serverSocket;
	
	public Server() {
		this.serverSocket = null;
		try {
			this.serverSocket = new ServerSocket(12345);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Socket clientSocket = null;
		
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				ClientThread clientThread = new ClientThread(clientCount, this, clientSocket);
				clientStreams.add(clientCount++, new ObjectOutputStream(clientSocket.getOutputStream()));
				new Thread(clientThread).start();
				System.out.println("New connection with: " + clientSocket.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void closeSocket(Socket client, int id) {
		clientStreams.remove(id);
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(int senderId, Message message) {
		System.out.println("Sending message:");
		System.out.println(message.getContent());
		for (int i = 0; i < clientStreams.size(); i++) {
			if (i != senderId) {
				ObjectOutputStream stream = clientStreams.get(i);
				try {
					stream.writeObject(message);
					stream.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void close() {
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
