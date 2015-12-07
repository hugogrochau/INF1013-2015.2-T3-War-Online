package org.puc.rio.war.server;

import org.puc.rio.war.model.Message;

public class MessageFactory {
	public static Message nameMessage(String name) {
		return new Message(Message.Header.NAME, name);
	}
	public static Message stateMessage(String state) {
		return new Message(Message.Header.STATE, state);
	}
}
