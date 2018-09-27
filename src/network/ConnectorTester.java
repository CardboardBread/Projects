package network;

import java.io.IOException;
import java.util.Scanner;

public class ConnectorTester {
	
	public static void main(String[] args) {
		Scanner console = new Scanner(System.in);
		boolean running = true;
		Connector user;
		try {
			user = new Connector(args[0], args[1], Integer.parseInt(args[2]));
			Thread thread = new Thread(user);
			thread.start();
						
			while(running) {
				String input = console.nextLine();
				String[] arguments = input.split(" ");
				
				switch(arguments[0]) {
				case "send":
					if (arguments.length > 1) {
						try {
							int clientIndex = Integer.parseInt(arguments[1]);
							for (int i = 2; i < arguments.length; i++) {
								user.send(clientIndex, arguments[i].getBytes());
								System.out.println("Sent " + arguments[i]);
							}
						} catch (NumberFormatException nfe) {
							for (int i = 1; i < arguments.length; i++) {
								user.send(arguments[i].getBytes());
								System.out.println("Sent " + arguments[i]);
							}
						}
					}
					
					break;
				case "disconnect":
					user.disconnect();
					running = false;
					break;
				}
			}
			console.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
