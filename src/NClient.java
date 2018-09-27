import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NClient extends NConnect {
	
	public static void main(String[] args) {
		try {
			Scanner console = new Scanner(System.in);
			NClient client = new NClient(args[0], args[1]);
			Thread thread = new Thread(client);
			thread.start();
						
			while(client.running) {
				String input = console.nextLine();
				String[] arguments = input.split(" ");
				
				switch(arguments[0]) {
				case "connect":
					client.connect(arguments[1], arguments[2]);
					break;
				case "send":
					for (int i = 1; i < arguments.length; i++) {
						client.send(arguments[i].getBytes());
					}
					break;
				case "disconnect":
					client.disconnect();
					break;
				case "exit":
					thread.interrupt();
					break;
				}
			}
			console.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private SocketChannel client;

	public NClient() throws IOException {
		super();
	}
	
	public NClient(InetSocketAddress address) throws IOException {
		this();
		connect(address);
	}
	
	public NClient(String address, int port) throws IOException {
		this(new InetSocketAddress(address, port));
	}
	
	public NClient(String address, String port) throws NumberFormatException, IOException {
		this(address, Integer.parseInt(port));
	}
	
	public void connect(InetSocketAddress address) throws IOException {
		client = SocketChannel.open();
		client.configureBlocking(false);
		
		String threadName = Thread.currentThread().getName();
		if (client.connect(address)) {
			System.out.print(threadName + " connected to: " + address);
		} else {
			System.out.print(threadName + " connecting to: " + address + "...");
			client.register(selector, SelectionKey.OP_CONNECT);
		}
	}
	
	public void connect(String address, int port) throws IOException {
		connect(new InetSocketAddress(address, port));
	}
	
	public void connect(String address, String port) throws NumberFormatException, IOException {
		connect(address, Integer.parseInt(port));
	}

	protected void disconnect() throws IOException {
		client.close();
	}

}
