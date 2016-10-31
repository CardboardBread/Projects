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
	
	public ClientChannel (String address, int port) {
		host = new InetSocketAddress(address, port);
	}
	
	public ClientChannel (InetSocketAddress address) {
		host = address;
	}
	
	public void run () {
		try {
			client = SocketChannel.open();
			client.configureBlocking(false);
			client.connect(host);
			
			String threadName = Thread.currentThread().getName();
			
			// if run from a client process, it will put trailing dots while it waits to connect
			System.out.print(threadName + " connecting to " + host + "...");
			while (!client.finishConnect()) {
				System.out.print(".");
			}
			System.out.println(" Connected.");
			
			// sample string
			messages = new String[] {threadName + ": the ride never ends1",threadName + ": the ride never ends2",threadName + ": the ride never ends3"};
		} catch (IOException e) {
			e.printStackTrace();
		}
		send(messages, 1);
	}
	
	public void send (String[] messages, int delay) {
		try {
			for (String str : messages) {
				ByteBuffer buffer = ByteBuffer.allocate(48);
				buffer.clear();
				buffer.put(str.getBytes());
				
				buffer.flip();
				
				// keeps writing until the buffer runs out of things to write
				while (buffer.hasRemaining()) {
					client.write(buffer);
				}
				// delay (cosmetic)
				Thread.sleep(delay);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect () throws IOException {
		client.close();
	}
}

class ServerChannel extends Thread {
	private Selector selector;
	private ServerSocketChannel server;
	private InetSocketAddress hostAddress;
	private ByteBuffer readBuffer = ByteBuffer.allocate(48);
	
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
		
		readBuffer.clear();
		
		int bytesRead = client.read(readBuffer);
		int totalBytesRead = bytesRead;
		
		// attempts to read as many bytes as the buffer is holding
		while (bytesRead > 0) {
			bytesRead = client.read(readBuffer);
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
			data[i] = readBuffer.get(i);
		}
		System.out.println("Received \"" + new String(data) + "\"");
		return data;
	}
}
