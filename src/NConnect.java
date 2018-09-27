import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

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
	
	protected void keyCheck() throws IOException {
		selector.select();
		
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

		while (keyIterator.hasNext()) {
			SelectionKey key = keyIterator.next();

			if (key.isAcceptable()) {
				accept(key);
			} else if (key.isConnectable()) {
				establish(key);
			} else if (key.isWritable()) {
				write(key);
			} else if (key.isReadable()) {
				read(key);
			}
			keyIterator.remove();
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
	
	protected void establish(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		int waiter = 0;
		
		while(!channel.finishConnect()) {
			if (waiter > 10) {
				System.out.println(".");
				waiter = 0;
			} else {
				waiter++;
			}
		}
		
		System.out.println("Connected");
		channel.register(selector, SelectionKey.OP_READ, null);
	}
	
	protected void send(byte[] data) throws IOException {
		for (SelectionKey key: selector.keys()) {
			if ((key.interestOps() & SelectionKey.OP_READ) != 0)
			write(key.channel().register(selector, SelectionKey.OP_WRITE, data));
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
