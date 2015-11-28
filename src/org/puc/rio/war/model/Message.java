package org.puc.rio.war.model;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 5492043138065551633L;
	private Header header;
	private String content;
	
	public Message(Header header, String content) {
		this.header = header;
		this.content = content;
	}
	
	public Header getHeader() {
		return this.header;
	}

	public String getContent() {
		return content;
	}

	public enum Header {
		NAME, STATE;
	}
}
