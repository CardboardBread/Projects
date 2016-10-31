package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Channel1 {
	public static final String ADDRESS = "localhost";
	public static final int PORT = 6767;
	public static final int clients = 100;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		ServerChannel server = new ServerChannel(ADDRESS, PORT);
		server.start();
		for (int i = 1; i <= clients; i++) {
			ClientChannel client = new ClientChannel(ADDRESS, PORT);
			client.start();
			Thread.sleep(1); // Delay before starting new client connection (protection for non-blocking clients)
		}
	}
}

class ClientChannel extends Thread {
	private InetSocketAddress host;
	private SocketChannel client;
	private String[] messages;
	private String threadName;
	
	public ClientChannel (String address, int port) {
		host = new InetSocketAddress(address, port);
	}
	
	public ClientChannel (InetSocketAddress address) {
		host = address;
	}
	
	public void run () {
		connect(host);
		messages = new String[] {threadName + ": the ride never ends1",threadName + ": the ride never ends2",threadName + ": the ride never ends3"};
		write(messages, 1);
	}
	
	/**
	 * Attempts to connect to the provided address, giving the user real time feedback on how long its taking to connect.
	 * @param destination Where the client is to connect to.
	 */
	public void connect (InetSocketAddress destination) {
		try {
			threadName = Thread.currentThread().getName();
			client = SocketChannel.open();
			client.configureBlocking(false);
			client.connect(destination);
			
			System.out.print(threadName + " connecting to " + destination + "...");
			while (!client.finishConnect()) {
				System.out.print(".");
			}
			System.out.println(" Connected.");
		}
		catch (IOException e) {
			System.out.println("Failed to connect to " + destination);
			e.printStackTrace();
		}
	}
	
	/**
	 * Continuously loads data into a byte buffer, then constructs a byte array of the buffer's data.
	 * @return The data the function has read.
	 * @throws IOException
	 */
	public byte[] read () throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(48);
		int bytesRead = client.read(buffer);
		int totalBytesRead = bytesRead;
		
		while (bytesRead > 0) {
			bytesRead =  client.read(buffer);
			totalBytesRead += bytesRead;
		}
		
		if (bytesRead == -1) {
			Socket socket = client.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			System.out.println("Connection closed by server: " + remoteAddr);
			client.close();
			return null;
		}
		
		byte[] data = new byte[totalBytesRead];
		for (int i = 0; i < totalBytesRead; i++) {
			data[i] = buffer.get(i);
		}
		System.out.println("Received \"" + new String(data) + "\"");
		return data;
			
	}
	
	/**
	 * Sends multiple strings to the connected server.
	 * @param messages An array containing all the messages the client wants to send.
	 * @param delay The delay between each message being sent.
	 */
	public void write (String[] messages, int delay) {
		try {
			for (String str : messages) {
				ByteBuffer buffer = ByteBuffer.allocate(48);
				buffer.clear();
				buffer.put(str.getBytes());
				
				buffer.flip();
				
				while (buffer.hasRemaining()) {
					client.write(buffer);
				}
				Thread.sleep(delay);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes the socket responsible for handling connections.
	 * @throws IOException
	 */
	public void disconnect () throws IOException {
		client.close();
	}
}

class ServerChannel extends Thread {
	private Selector selector;
	private ServerSocketChannel server;
	private InetSocketAddress hostAddress;
	
	public ServerChannel (String address, int port) throws IOException {
		selector = Selector.open();
		
		server = ServerSocketChannel.open();
		server.configureBlocking(false);
		
		hostAddress = new InetSocketAddress(address, port);
		server.socket().bind(hostAddress);
		System.out.println("Server started on: " +  hostAddress);
		
		server.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	public void run () {
		while (true) {
			try {
				// obtains all keys that are satisfied
				selector.select();
				
				// creates an iterator to move through all the keys
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
				
				while (keyIterator.hasNext()) {
					SelectionKey key = keyIterator.next();
					
					if (key.isAcceptable()) {
						accept(key);
					} else if (key.isReadable()) {
						read(key);
					}
					
					// removes key from iterator, so you dont hit it again
					keyIterator.remove();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void accept (SelectionKey key) throws IOException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel client = server.accept();
		if (client != null) {
			client.configureBlocking(false);
			
			Socket socket = client.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			System.out.println("Connected to: " +  remoteAddr);
			
			// registers the client a reading key
			client.register(selector, SelectionKey.OP_READ);
		}
	}
	
	public byte[] read (SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(48);
		
		buffer.clear();
		
		int bytesRead = client.read(buffer);
		int totalBytesRead = bytesRead;
		
		// attempts to read as many bytes as the buffer is holding
		while (bytesRead > 0) {
			bytesRead = client.read(buffer);
			totalBytesRead += bytesRead;
		}
		
		// when the read brings a -1, the user closed the connection, so we close the sockets and cancel the keys
		if (bytesRead == -1) {
			Socket socket = client.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			System.out.println("Connection closed by client: " + remoteAddr);
			client.close();
			key.cancel();
			return null;
		}
		
		// copies the data from the buffer into a byte array
		byte[] data = new byte[totalBytesRead];
		for (int i = 0; i < totalBytesRead; i++) {
			data[i] = buffer.get(i);
		}
		System.out.println("Received \"" + new String(data) + "\"");
		return data;
	}
}
