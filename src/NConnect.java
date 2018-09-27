import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public abstract class NConnect implements Runnable {
	
	protected static final int BUFFER_SIZE = 48;
	protected Selector selector;	
	protected boolean running;
	protected byte[] sending;
	protected byte[] received;
	
	public NConnect() throws IOException {
		selector = Selector.open();
		running = true;
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
	
	abstract protected void keyCheck() throws IOException;
	
	protected void send(byte[] data) throws IOException {
		for (SelectionKey key: selector.keys()) {
			if ((key.interestOps() & SelectionKey.OP_READ) != 0)
			write(key.channel().register(selector, SelectionKey.OP_WRITE, data));
		}
	}
	
	protected void accept(SelectionKey key) throws IOException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel client = server.accept();
		
		if (client != null) {
			client.configureBlocking(false);
			SocketAddress remoteAddress = client.getRemoteAddress();
			System.out.println("Accepted connection from: " + remoteAddress);
			client.register(selector, SelectionKey.OP_READ, null);
		}
	}
	
	protected void write(SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		buffer.clear();
		buffer.put((byte[]) key.attachment());
		buffer.flip();
		
		while (buffer.hasRemaining()) {
			client.write(buffer);
		}
		System.out.println("Sent: " + key.attachment() + " to: " + client.getRemoteAddress());
		client.register(selector, SelectionKey.OP_READ, null);
	}
	
	protected byte[] read(SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		int bytesRead = client.read(buffer);
		
		while (bytesRead > 0) {
			bytesRead = client.read(buffer);
		}
		
		Socket socket = client.socket();
		SocketAddress remoteAddress = socket.getRemoteSocketAddress();
		
		if (bytesRead == -1) {
			System.out.println("Connection Closed by: " + remoteAddress);
			client.close();
			return null;
		}
		
		System.out.println("Received \"" + new String(buffer.array()) + "\" from: " + remoteAddress);
		return buffer.array();
	}
	
	abstract protected void disconnect() throws IOException;
}
