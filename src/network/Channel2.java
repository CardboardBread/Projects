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
	public static final int clients = 700;
	public static final TCPSocketChannelType Client = TCPSocketChannelType.Client;
	public static final TCPSocketChannelType Server = TCPSocketChannelType.Server;
	public static void main(String[] args) throws IOException, InterruptedException {
		TCPSocketChannel server = new TCPSocketChannel(ADDRESS, PORT, Server);
		server.start();
		for (int i = 1; i <= clients; i++) {
			TCPSocketChannel client = new TCPSocketChannel(ADDRESS, PORT, Client);
			client.start();
			Thread.sleep(1); // Delay before starting new client connection (protection for non-blocking clients)
		}
	}
}

class TCPSocketChannel extends Thread {
	private int bufferSize = 48;
	private Selector selector;
	private TCPSocketChannelType function;
	private ServerSocketChannel server;
	private SocketChannel client;
	private byte[] sending;
	@SuppressWarnings("unused")
	private byte[] received;
	
	
	/**
	 * Constructor method responsible for initializing the function and Inet location of the thread.
	 * @param address String responsible for the universal destination/source of the client/server.
	 * @param port The port the client will connect to, or the server will host on.
	 * @param function Enum type identifying whether the thread is acting as a client or server.
	 * @throws IOException
	 */
	public TCPSocketChannel (String address, int port, TCPSocketChannelType function) throws IOException {
		this.function = function;
		selector = Selector.open();
		if (function == TCPSocketChannelType.Server) {
			host(new InetSocketAddress(address, port));
		} else {
			connect(new InetSocketAddress(address, port));
		}
	}
	
	/**
	 * Thread extended method responsible for consistently checking for satisfied keys.
	 */
	public void run () {
		while (true) {
			try {
				keyCheck();
				Thread.sleep(10);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * Selects and then iterates through all keys that have at least one of their 4 flags satisfied.
	 * @throws IOException
	 */
	public void keyCheck () throws IOException {
		selector.select();
		
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
		
		while (keyIterator.hasNext()) {
			SelectionKey key = keyIterator.next();
			
			if (key.isAcceptable()) {
				accept(key);
			} else if (key.isConnectable()) {
				welcome(key);
			} else if (key.isReadable()) {
				received = read(key);
			} else if (key.isWritable()) {
				write(key);
			}
			keyIterator.remove();
		}
	}
	
	/**
	 * Key handler for when the server has a client attempting to connect to it.
	 * @param key The key bound to the server registered for accepting connections.
	 * @throws IOException
	 */
	private void accept (SelectionKey key) throws IOException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel client = server.accept();
		if (client != null) {
			client.configureBlocking(false);
			
			Socket socket = client.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			System.out.println("Accepted connection from: " +  remoteAddr);
			
			client.register(selector, SelectionKey.OP_READ);
		}
	}
	
	/**
	 * Public call supposed to tie into registering an object to satisfy key.isConnectable.
	 * @param socket
	 * @throws IOException
	 */
	public void candidate (SocketChannel socket) throws IOException {
		
	}
	
	/**
	 * Creates a server bound to the provided address, then registers the server to accept connections.
	 * @param address The location the server will bind to.
	 * @throws IOException
	 */
	private void host (InetSocketAddress address) throws IOException {
		server = ServerSocketChannel.open();
		server.configureBlocking(false);
		server.bind(address);
		
		System.out.println("Server started on: " + address);
		
		server.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	/**
	 * Attempts to connect to the provided address as a client, giving the user real time feedback on how long its taking to connect.
	 * A connection registration will also be made, in the event that the connection succeeds.
	 * @param address The location the client is to connect to.
	 * @throws IOException
	 */
	public void connect (InetSocketAddress address) throws IOException {
		client = SocketChannel.open();
		client.configureBlocking(false);
		client.connect(address);
		
		String threadName = Thread.currentThread().getName();
		System.out.print(threadName + " connecting to " + address + "...");
		while (!client.finishConnect()) {
			System.out.print(".");
		}
		System.out.println(" Connected.");
		client.register(selector, SelectionKey.OP_CONNECT);
	}
	
	/**
	 * Public call to be made once a connection has succeeded. //TODO: Supposed to be called when a key isConnectable, does not trigger
	 * @throws ClosedChannelException 
	 */
	public void welcome (SelectionKey key) throws ClosedChannelException {
		SocketChannel client = (SocketChannel) key.channel();
		send(client.toString().getBytes());
	}
	
	/**
	 * Identifies, then writes data sent from a client into a byte array.
	 * @param key The identifier for the client sending data.
	 * @return All data read from the client.
	 * @throws IOException
	 */
	private byte[] read (SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		int bytesRead = client.read(buffer);
		int totalBytesRead = bytesRead;
		
		while (bytesRead > 0) {
			bytesRead =  client.read(buffer);
			totalBytesRead += bytesRead;
		}
		
		if (bytesRead == -1) {
			Socket socket = client.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			System.out.println("Connection closed by: " + remoteAddr);
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
	 * Public call to register data to be read from any client.
	 * @throws ClosedChannelException 
	 */
	public void receive () throws ClosedChannelException {
		if (function == TCPSocketChannelType.Client) {
			client.register(selector, SelectionKey.OP_READ);
		} else {
			server.register(selector, SelectionKey.OP_READ);
		}
	}
	
	/**
	 * Identifies a client's socket, and sends data created from the send method.
	 * @param key The identifier responsible for sending the right data to the right client.
	 * @throws IOException
	 */
	private void write (SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		buffer.clear();
		buffer.put(sending);
		
		buffer.flip();
		
		while (buffer.hasRemaining()) {
			client.write(buffer);
		}
	}
	
	/**
	 * Public call to register data to sent.
	 * @param data The byte array to be registered for sending.
	 * @throws ClosedChannelException
	 */
	public void send (byte[] data) throws ClosedChannelException {
		sending = data;
		if (function == TCPSocketChannelType.Client) {
			client.register(selector, SelectionKey.OP_WRITE);
		} else {
			server.register(selector, SelectionKey.OP_WRITE);
		}
		
	}
	
	/**
	 * Closes the socket responsible for handling connections.
	 * @throws IOException
	 */
	public void disconnect () throws IOException {
		server.close();
		client.close();
	}
}
