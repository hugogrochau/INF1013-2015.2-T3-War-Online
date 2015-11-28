package org.puc.rio.war.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.puc.rio.war.WarGame;
import org.puc.rio.war.model.Message;

public class MessageReceiver implements Runnable {
	private ObjectInputStream serverStream;

	public MessageReceiver(InputStream serverStream) throws IOException {
		this.serverStream = new ObjectInputStream(serverStream);
	}

	public void closeStream() throws IOException {
		serverStream.close();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Message msg = (Message) serverStream.readObject();
				if (msg.getHeader() == Message.Header.STATE) {
					System.out.println("Received message:");
					System.out.println(msg.getContent());
					WarGame.getInstance().loadGame(msg.getContent());
				} else if (msg.getHeader() == Message.Header.NAME) {
					WarGame.getInstance().addPlayer(msg.getContent(), false);
				}
			} catch (ClassNotFoundException classNot) {
				System.err.println("data received in unknown format");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
