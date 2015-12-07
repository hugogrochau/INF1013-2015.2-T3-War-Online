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
					System.out.println("Received message:");	
					System.out.println(msg.getContent());
					if (msg.getHeader() == Message.Header.NAME) {
						WarGame.getInstance().addPlayer(msg.getContent(), false);
					} else {
						boolean first = !WarGame.getInstance().hasStarted();
						WarGame.getInstance().loadGame(msg.getContent());
						WarGame.getInstance().getWarFrame().update(first);
					}
			} catch (ClassNotFoundException classNot) {
				System.err.println("Data received in unknown format");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
