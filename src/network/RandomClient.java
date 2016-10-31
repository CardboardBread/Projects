package network;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class RandomClient
{
	public static byte target = -1;
	public static TCPSocket socket;
	public static byte location;
	
	private static final int PORT = 90;
	private static final String SERVER_ADDRESS = "localhost";
	private static ReceiveThread receive;
	private static SendThread send;
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		socket = new TCPSocket(new Socket(SERVER_ADDRESS, PORT));
		receive = new ReceiveThread();
		send = new SendThread();
		receive.start();
		send.start();
	}
	
	public static void leave ()
	{
		try
		{
			socket.getSocket().close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

class ReceiveThread extends Thread
{
	private byte[] received;
	private byte userCount;
	private byte location;
	private boolean listen;
	
	public void run()
	{
		listen = true;
		userCount = 0;
		while (listen)
		{
			try
			{
				received = RandomClient.socket.receivePacket();
				receive(received);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	private void receive (byte[] data)
	{
		byte packetType = data[0];
		//byte destination = data[1];
		byte origin = data[2];
		byte dataVal = data[3];
		
		switch (packetType)
		{
		case 0: // send
			System.out.println("Received \"" + dataVal + "\" from Client " + origin + ".");
			break;
		case 1: // receive
			location = origin;
			userCount = dataVal;
			System.out.println("Received userlist of " + userCount + " users, my location is " + location + ".");
			RandomClient.location = origin;
			RandomClient.target = getTarget(userCount);
			break;
		default: // header not recognized
			System.out.println("Received invalid packet " + dataVal + " from " + origin + ".");
			break;
		}
	}
	
	private byte getTarget (byte users)
	{
		Random random = new Random();
		byte subject = (byte) random.nextInt(users);
		if (subject == location)
		{
			if (users > 0)
			{
				return -1;
			}
			else
			{
				return -1;
			}
		}
		else
		{
			System.out.println("Targeted Client " + subject + ".");
			return subject;
		}
	}
}

class SendThread extends Thread
{
	private byte[] sending;
	private byte location;
	private byte target;
	private boolean send;
	
	public void run()
	{
		send = true;
		update();
		while (send)
		{
			Random random = new Random();
			update();
			try
			{
				if (target != -1)
				{
					send((byte) random.nextInt());
				}
				else
				{
					//System.out.println("No users to send data to.");
				}
				Thread.sleep(200);
			}
			catch (IOException | InterruptedException e)
			{
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	private void update ()
	{
		target = RandomClient.target;
		location = RandomClient.location;
	}
	
	private void send (byte data) throws IOException
	{
		sending = new byte[] {0, target, location, data};
		System.out.println("Sending \"" + data + "\" to Client " + target);
		RandomClient.socket.sendPacket(sending);
	}
}