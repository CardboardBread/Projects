import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.Scanner;

public class NServer extends NConnect {
	
	public static void main(String[] args) {
		try {
			Scanner console = new Scanner(System.in);
			NServer server = new NServer(args[0], args[1]);
			Thread thread = new Thread(server);
			thread.start();
						
			while(server.running) {
				String input = console.nextLine();
				String[] arguments = input.split(" ");
				
				switch(arguments[0]) {
				case "host":
					server.host(arguments[1], arguments[2]);
					break;
				case "send":
					for (int i = 1; i < arguments.length; i++) {
						server.send(arguments[i].getBytes());
					}
					break;
				case "disconnect":
					server.disconnect();
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
	
	private ServerSocketChannel server;

	public NServer() throws IOException {
		super();
	}
	
	public NServer(InetSocketAddress address) throws IOException {
		this();
		host(address);
	}
	
	public NServer(String address, int port) throws IOException {
		this(new InetSocketAddress(address, port));
	}
	
	public NServer(String address, String port) throws NumberFormatException, IOException {
		this(address, Integer.parseInt(port));
	}

	public void host(InetSocketAddress address) throws IOException {
		server = ServerSocketChannel.open();
		server.configureBlocking(false);
		server.bind(address);
		
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + " hosting on: " + address);
		server.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	public void host(String address, int port) throws IOException {
		host(new InetSocketAddress(address, port));
	}
	
	public void host(String address, String port) throws NumberFormatException, IOException {
		host(address, Integer.parseInt(port));
	}

	protected void disconnect() throws IOException {
		server.close();
	}

}
