package sockets;

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
	public static final int clients = 50;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		ServerChannel server = new ServerChannel(ADDRESS, PORT);
		server.start();
		for (int i = 0; i < clients; i++) {
			ClientChannel client = new ClientChannel(ADDRESS, PORT);
			client.start();
			Thread.sleep(100);
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
			client.connect(host);
			System.out.println("Client connected to " + host);
			String threadName = Thread.currentThread().getName();
			messages = new String[] {threadName + ": test1",threadName + ": test2",threadName + ": test3"};
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			for (String str : messages) {
				byte[] message = new String(str).getBytes();
				ByteBuffer buffer = ByteBuffer.wrap(message);
				client.write(buffer);
				System.out.println(str);
				buffer.clear();
				Thread.sleep(5000);
			}
			client.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void send (String[] messages) {
		sendHandler(messages, 1);
	}
	
	public void send (String[] messages, int delay) {
		sendHandler(messages, delay);
	}
	
	public void sendHandler (String[] messages, int delay) {
		try {
			for (String str : messages) {
				byte[] byteMessage = new String(str).getBytes();
				ByteBuffer buffer = ByteBuffer.wrap(byteMessage);
				client.write(buffer);
				buffer.clear();
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
				selector.select();
				
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
				
				while (keyIterator.hasNext()) {
					SelectionKey key = keyIterator.next();
					
					if (key.isAcceptable()) {
						accept(key);
					} else if (key.isReadable()) {
						read(key);
					}
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
			
			client.register(selector, SelectionKey.OP_READ);
		}
	}
	
	public void read (SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		
		readBuffer.clear();
		
		int bytesRead = client.read(readBuffer);
		int totalBytesRead = bytesRead;
		
		while (bytesRead > 0) {
			bytesRead = client.read(readBuffer);
			totalBytesRead += bytesRead;
		}
		
		if (bytesRead == -1) {
			Socket socket = client.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			System.out.println("Connection closed by client: " + remoteAddr);
			client.close();
			key.cancel();
			return;
		}
		
		byte[] data = new byte[totalBytesRead];
		System.arraycopy(readBuffer, 0, data, 0, totalBytesRead);
		System.out.println("Received " + new String(data));
	}
}
