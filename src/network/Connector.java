import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class Connector implements Runnable {
	private static int bufferSize = 48;
	private Selector selector;
	private AbstractSelectableChannel socketConn;
	private byte[] sending;
	private byte[] received;
	private boolean running;
	private ArrayList<SocketChannel> connected;
	
	public Connector(String function, String address, int port) throws IOException {
		selector = Selector.open(); 
		running = true;
		connected = new ArrayList<SocketChannel>();
		
		if (function.equals("connect")) {
			connect(address, port);
		} else if (function.equals("host")) {
			host(address, port);
		} else {
			throw new IOException();
		}
	}

	/**
	 * Selects and then iterates through all keys that have at least one of
	 * their 4 flags satisfied.
	 * 
	 * @throws IOException
	 */
	public void keyCheck() throws IOException {
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
				read(key);
			} else if (key.isWritable()) {
				write(key);
			}
			keyIterator.remove();
		}
	}
	
	public void run() {
		while(running) {
			try {
				keyCheck();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Key handler for when the server has a client attempting to connect to it.
	 * 
	 * @param key
	 *            The key bound to the server registered for accepting
	 *            connections.
	 * @throws IOException
	 */
	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel client = server.accept();
		if (client != null) {
			client.configureBlocking(false);
			connected.add(client);

			Socket socket = client.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			System.out.println("Accepted connection from: " + remoteAddr);

			client.register(selector, SelectionKey.OP_READ);
		}
	}

	/**
	 * Public call to translate outside input into usable data, which is then
	 * passed onto the appropriate private method.
	 * @param address The IP address the client is to host on. 
	 * @param port The port the client is to host on. 
	 * @throws IOException
	 */
	public void host(String address, int port) throws IOException {
		host(new InetSocketAddress(address, port));
	}

	/**
	 * Creates a server bound to the provided address, then registers the server
	 * to accept connections.
	 * 
	 * @param address
	 *            The location the server will bind to.
	 * @throws IOException
	 */
	private void host(InetSocketAddress address) throws IOException {
		socketConn = ServerSocketChannel.open();
		socketConn.configureBlocking(false);
		((ServerSocketChannel) socketConn).bind(address);

		System.out.println("Server started on: " + address);

		socketConn.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	/**
	 * Public call to translate outside input into usable data, which is then
	 * passed onto the appropriate private method.
	 * @param address The IP address the client is to connect to. 
	 * @param port The port the client is to connect to. 
	 * @throws IOException
	 */
	public void connect(String address, int port) throws IOException {
		connect(new InetSocketAddress(address, port));
	}

	/**
	 * Attempts to connect to the provided address as a client, giving the user
	 * real time feedback on how long its taking to connect. A connection
	 * registration will also be made, in the event that the connection
	 * succeeds.
	 * 
	 * @param address
	 *            The location the client is to connect to.
	 * @throws IOException
	 */
	private void connect(InetSocketAddress address) throws IOException {
		socketConn = SocketChannel.open();
		socketConn.configureBlocking(false);
		((SocketChannel) socketConn).connect(address);

		String threadName = Thread.currentThread().getName();
		System.out.print(threadName + " connecting to " + address + ".");
		socketConn.register(selector, SelectionKey.OP_CONNECT);
	}

	/**
	 * Public call to be made once a connection has succeeded. //TODO: Supposed
	 * to be called when a key isConnectable, does not trigger
	 * 
	 * @throws ClosedChannelException
	 */
	private void welcome(SelectionKey key) throws IOException {
		int waiter = 0;
		while (!((SocketChannel) socketConn).finishConnect()) {
			waiter++;
			if (waiter > 100) {
				System.out.print(".");
				waiter = 0;
			}
		}
		System.out.println("Connected.");
		socketConn.register(selector, SelectionKey.OP_READ);
	}

	/**
	 * Identifies, then writes data sent from a client into a byte array.
	 * 
	 * @param key
	 *            The identifier for the client sending data.
	 * @return All data read from the client.
	 * @throws IOException
	 */
	private byte[] read(SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		int bytesRead = client.read(buffer);
		int totalBytesRead = bytesRead;

		while (bytesRead > 0) {
			bytesRead = client.read(buffer);
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
		System.out.println("Received \"" + new String(data) + "\" from " + client.socket().getRemoteSocketAddress());
		return data;
	}

	/**
	 * Public call to register data to be read from any client.
	 * 
	 * @throws ClosedChannelException
	 */
	public void startReceiving() throws ClosedChannelException {
		socketConn.register(selector, SelectionKey.OP_READ);
	}

	/**
	 * Identifies a client's socket, and sends data created from the send
	 * method.
	 * 
	 * @param key
	 *            The identifier responsible for sending the right data to the
	 *            right client.
	 * @throws IOException
	 */
	private void write(SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		buffer.clear();
		buffer.put(sending);

		buffer.flip();

		while (buffer.hasRemaining()) {
			client.write(buffer);
		}
		key.cancel();
	}

	/**
	 * Public call to register data to sent.
	 * 
	 * @param data
	 *            The byte array to be registered for sending.
	 * @throws ClosedChannelException
	 */
	public void send(byte[] data) throws ClosedChannelException {
		sending = data;
		socketConn.register(selector, SelectionKey.OP_WRITE);
	}
	
	public void send(SocketChannel channel, byte[] data) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		buffer.clear();
		buffer.put(data);
		
		buffer.flip();
		
		while (buffer.hasRemaining()) {
			channel.write(buffer);
		}
	}
	
	public void send(int index, byte[] data ) throws IOException {
		send(connected.get(index), data);
	}

	/**
	 * Closes the socket responsible for handling connections.
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		socketConn.close();
	}
}
