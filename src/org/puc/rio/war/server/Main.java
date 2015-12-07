package org.puc.rio.war.server;

public class Main {

	private static Server server;

	public static void main(String args[]) {
		System.out.println("Starting war server");
		server = new Server();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				server.close();
			}
		}));
	}
}
