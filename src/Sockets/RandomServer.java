package Sockets;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomServer
{	
	public static List<Thread> sessions;
	
	private static final int port = 90;
	private static TCPSocket incoming;
	private static ServerSocket serverSocket;
	private static boolean listen = true;
	
	public static void main(String[] args)
	{
		sessions = new ArrayList<Thread>();
		
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
	private byte size;
	public boolean user;
	
	public ServerThread(TCPSocket subject)
	{
		incoming = subject;
	}
	
	public void start()
	{
		size = (byte) RandomServer.sessions.size();
		user = true;
	}
	
	@Override
	public void run()
	{			
		while (user)
		{
			try
			{
				received = incoming.receivePacket();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			if (size != RandomServer.sessions.size())
			{
				size = (byte) RandomServer.sessions.size();
				sending = new byte[] {2, (byte) size};
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
		
	}
	
	public void serverReceive (byte[] data)
	{
		
	}
}