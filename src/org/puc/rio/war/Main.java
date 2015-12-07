package org.puc.rio.war;

public class Main {
	public static void main(String args[]) {
		System.out.println("War");
		WarGame.getInstance();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				WarGame.getInstance().getClient().close();
			}
		}));
	}
}
