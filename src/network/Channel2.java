package network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Channel2 {
	public static final String ADDRESS = "localhost";
	public static final int PORT = 6767;
	public static final int clients = 1;
	
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
	private InetSocketAddress destination;
	private Selector selector;
	private SocketChannel client;
	private byte[] sending;
	private byte[] received;
	
	public ClientChannel (String address, int port) throws IOException {
		selector = Selector.open();
		establish(new InetSocketAddress(address, port));
	}
	
	public ClientChannel (InetSocketAddress address) throws IOException {
		selector = Selector.open();
		establish(address);
	}
	
	public void run () {
		while (true) {
			try {
				keyCheck();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void keyCheck () throws IOException {
		selector.select();
		
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
		
		while (keyIterator.hasNext()) {
			SelectionKey key = keyIterator.next();
			
			if (key.isAcceptable()) {
				accept(key);
			} else if (key.isConnectable()) {
				connect(key);
			} else if (key.isReadable()) {
				received = read(key);
			} else if (key.isWritable()) {
				write(key);
			}
			keyIterator.remove();
		}
	}
	
	
	
	private void accept (SelectionKey key) {
		
	}
	
	public void candidate (Socket socket) {
		
	}
	
	/**
	 * Attempts to connect to the provided address, giving the user real time feedback on how long its taking to connect.
	 * @param destination Where the client is to connect to.
	 * @throws IOException 
	 */
	private void connect (SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		String threadName = Thread.currentThread().getName();
		client.connect(destination);
		
		System.out.print(threadName + " connecting to " + destination + "...");
		while (!client.finishConnect()) {
			System.out.print(".");
		}
		System.out.println(" Connected.");
	}
	
	public void establish (InetSocketAddress address) throws IOException {
		destination = address;
		client = SocketChannel.open();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_CONNECT);
	}
	
	/**
	 * Continuously loads data into a byte buffer, then constructs a byte array of the buffer's data.
	 * @return The data the function has read.
	 * @throws IOException
	 */
	private byte[] read (SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
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
	 * Public call to register client to receive data.
	 * @throws ClosedChannelException 
	 */
	public void receive () throws ClosedChannelException {
		client.register(selector, SelectionKey.OP_READ);
	}
	
	/**
	 * Sends a byte array to the connected server.
	 * @param messages An array containing all the bytes the client wants to send.
	 * @param delay The delay between each message being sent.
	 * @throws IOException 
	 */
	private void write (SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(48);
		buffer.clear();
		buffer.put(sending);
		
		buffer.flip();
		
		while (buffer.hasRemaining()) {
			client.write(buffer);
		}
	}
	
	/**
	 * Public call to register data for the client to send.
	 * @param data The bytes to be registered for sending.
	 * @throws ClosedChannelException
	 */
	public void send (byte[] data) throws ClosedChannelException {
		sending = data;
		client.register(selector, SelectionKey.OP_WRITE);
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
	
	public ServerChannel (String address, int port) throws IOException {
		connect(new InetSocketAddress(address, port));
	}
	
	public ServerChannel (InetSocketAddress source) throws IOException {
		connect(source);
	}
	
	public void run () {
		while (true) {
			try {
				keyCheck();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void keyCheck () throws IOException {
		selector.select();
		
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
		
		while (keyIterator.hasNext()) {
			SelectionKey key = keyIterator.next();
			
			if (key.isAcceptable()) {
				accept(key);
			} else if (key.isReadable()) {
				read(key);
			} else if (key.isWritable()) {
				
			}
			keyIterator.remove();
		}
	}
	
	/**
	 * Handles the provided key, which contains an accept flag, registering the client's socket in the selector.
	 * @param key The accept flagged key.
	 * @throws IOException
	 */
	public void accept (SelectionKey key) throws IOException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel client = server.accept();
		if (client != null) {
			client.configureBlocking(false);
			
			Socket socket = client.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			System.out.println("Connected to: " +  remoteAddr);
			
			client.register(selector, SelectionKey.OP_READ);
		}
	}
	
	/**
	 * Starts and binds the server onto the provided address, once that is completed, the server will be registered with the selector.
	 * @param bind The address the server wants to bind to.
	 * @throws IOException
	 */
	public void connect (InetSocketAddress bind) throws IOException {
		selector = Selector.open();
		server = ServerSocketChannel.open();
		server.configureBlocking(false);
		
		server.socket().bind(bind);
		System.out.println("Server started on: " +  bind);
		
		server.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public byte[] read (SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(48);
		
		buffer.clear();
		
		int bytesRead = client.read(buffer);
		int totalBytesRead = bytesRead;
		
		while (bytesRead > 0) {
			bytesRead = client.read(buffer);
			totalBytesRead += bytesRead;
		}
		
		if (bytesRead == -1) {
			Socket socket = client.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			System.out.println("Connection closed by client: " + remoteAddr);
			client.close();
			key.cancel();
			return null;
		}
		
		byte[] data = new byte[totalBytesRead];
		for (int i = 0; i < totalBytesRead; i++) {
			data[i] = buffer.get(i);
		}
		System.out.println("Received \"" + new String(data) + "\"");
		return data;
	}
	
	public void write () {
		
	}
}
