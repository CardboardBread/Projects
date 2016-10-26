package Sockets;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class RandomClient
{
	private static final int port = 90;
	private static final String address = "localhost";
	private static boolean listen = true;
	private static byte userCount = -1;
	private static byte userTarget;
	private static byte location;
	
	public static byte[] received;
	public static byte[] sending;
	
	public static void main(String[] args) throws IOException
	{
		TCPSocket socket = new TCPSocket(new Socket(address, port));
		Random rand = new Random();
		
		while (listen)
		{
			try
			{
				received = socket.receivePacket();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			if (userCount != -1)
			{
				sendTarget(userCount);
				send((byte) rand.nextInt());
			}
		}
	}
	
	public static void receive (byte[] data)
	{
		int origin = data[2];
		int destination = data[1];
		
		switch (data[0])
		{
		case 0: // send
			System.out.println("Received an invalid packet " + data[4]);
			break;
		case 1: // receive
			System.out.println("Received " + data[4] + " from " + data[2]);
			break;
		case 2: // userlist
			location = data[3];
			userCount = data[4];
			break;
		}
	}
	
	public static void send (byte data)
	{
		sending = new byte[] {0, userTarget, location, data};
	}
	
	public static byte sendTarget (byte users)
	{
		Random random = new Random();
		byte subject = (byte) random.nextInt(users);
		return subject;
	}
}
