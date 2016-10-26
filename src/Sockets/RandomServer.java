package Sockets;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomServer
{	
	public static List<ServerThread> sessions;
	
	private static final int port = 90;
	private static TCPSocket incoming;
	private static ServerSocket serverSocket;
	private static boolean listen = true;
	
	public static void main(String[] args)
	{
		sessions = new ArrayList<ServerThread>();
		
		try
		{
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		while (listen)
		{
			try
			{
				incoming = new TCPSocket(serverSocket.accept());
				sessions.add(new ServerThread(incoming));
				sessions.get(sessions.size() - 1).run();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}

class ServerThread extends Thread
{
	public TCPSocket incoming;
	public byte[] received;
	public byte[] sending;
	
	private boolean user;
	private int size;
	private int location;
	
	public ServerThread(TCPSocket subject)
	{
		incoming = subject;
	}
	
	public void start()
	{
		size = RandomServer.sessions.size();
		user = true;
		location = size - 1;
	}
	
	@Override
	public void run()
	{			
		while (user)
		{
			try
			{
				received = incoming.receivePacket();
				if (received[0] == 0)
				{
					serverSend(received);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			if (size != RandomServer.sessions.size())
			{
				size = RandomServer.sessions.size();
				sending = new byte[] {2, -1, (byte) location, (byte) size};
				try
				{
					incoming.sendPacket(sending);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
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
		try
		{
			incoming.sendPacket(data);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}