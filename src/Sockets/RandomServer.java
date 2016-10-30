package sockets;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// First byte is packet type [0]
// Second byte is destination id [1]
// Third byte is source id [2]
// Fourth byte is data [3]

public class RandomServer
{	
	public static List<ServerThread> sessions;
	
	private static final int PORT = 90;
	private static TCPSocket incoming;
	private static ServerSocket serverSocket;
	private static boolean listen;
	
	public static void main(String[] args)
	{
		sessions = new ArrayList<ServerThread>();
		listen = true;
		
		try
		{
			serverSocket = new ServerSocket(PORT);
			System.out.println("Starting server on port " + PORT + ".");
		}
		catch (IOException e)
		{
			System.out.println("Failed to bind server to port" + PORT + ".");
			e.printStackTrace();
		}
		
		while (listen)
		{
			try
			{
				Socket accept = serverSocket.accept();
				incoming = new TCPSocket(accept);
				ServerThread server = new ServerThread(incoming);
				server.start();
				sessions.add(server);
				System.out.println("Accepted connection from client " + (sessions.size() - 1) + ".");
			}
			catch (IOException e)
			{
				System.out.println("Failed to accept client.");
				e.printStackTrace();
			}
			finally
			{
				globalUpdate();
			}
		}
		
		if (!listen)
		{
			try
			{
				serverSocket.close();
				System.out.println("Closing server.");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void globalUpdate ()
	{
		for (ServerThread thread : sessions)
		{
			thread.userUpdate();
		}
	}
}

class ServerThread extends Thread
{
	public TCPSocket incoming;
	public byte[] received;
	public byte[] sending;
	
	private boolean user = true;
	private byte size;
	private byte location;
	
	public ServerThread(TCPSocket subject)
	{
		incoming = subject;
	}

	@Override
	public void run()
	{			
		size = (byte) RandomServer.sessions.size();
		location = (byte) (size - 1);
		while (user)
		{
			try
			{	
				received = incoming.receivePacket();
				//System.out.println("Server " + location + " sending packet to Server " + received[1] + ".");
				if (received[0] == 0)
				{
					serverSend(received);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.exit(0);
			}
			
			if (incoming.getSocket().isClosed())
			{
				user = false;
				try
				{
					incoming.getSocket().close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void serverSend (byte[] data)
	{
		RandomServer.sessions.get(data[1]).serverReceive(data);
	}
	
	public void serverReceive (byte[] data)
	{
		//System.out.println("Server " + location + " receiving packet from Server " + data[2] + ".");
		try
		{
			incoming.sendPacket(data);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void userUpdate ()
	{
		size = (byte) RandomServer.sessions.size();
		sending = new byte[] {1, location, location, size};
		System.out.println("Sending userlist to client " + location +  ".");
		try
		{
			incoming.sendPacket(sending);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}