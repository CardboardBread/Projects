package sockets;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
 
public class Server3 {
    public static void main(String args[]) throws Exception {
        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    // Creates the server socket that accepts new connections
                    ServerSocket welcomeSocket = new ServerSocket(6789);
                    // The accepted connection
                    TCPSocket sock = new TCPSocket(welcomeSocket.accept());
                    System.out.println("Accepted");
 
                    // Receives the bytes that the client sends
                    byte[] received = sock.receivePacket();
 
                    // Prints the received bytes
                    System.out.print("Server Received: [");
                    for (int i = 0; i < received.length; i++) {
                        System.out.print(received[i] + (i == received.length - 1 ? "]" : ", "));
                        received[i] += 1;
                    }
                    System.out.println();
 
                    // Sends back the same data, but each byte has 1 added to it
                    sock.sendPacket(received);
 
                    // Closes the server-side sockets
                    sock.getSocket().close();
                    welcomeSocket.close();
 
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start(); // Runs the server
 
        // Connects the client socket to the server's socket
        TCPSocket sock = new TCPSocket(new Socket("localhost", 6789));
 
        // Writes a random array of bytes to the server
        byte[] sending = new byte[] { 1, 3, 55, 100, -20, Byte.MAX_VALUE };
        sock.sendPacket(sending);
        sending = sock.receivePacket();
 
        // Prints what the client received back
        System.out.print("Client Received: [");
        for (int i = 0; i < sending.length; i++) {
            System.out.print(sending[i] + (i == sending.length - 1 ? "]" : ", "));
        }
        System.out.println();
 
        // Closes the client socket
        sock.getSocket().close();
 
        System.out.println("Program Terminated");
    }
}
 
class TCPSocket {
    private Socket sock;
 
    private OutputStream output;
    private InputStream input;
 
    public TCPSocket(Socket socket) {
        // Gets output stream of the socket
        try {
            output = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        // Gets input stream of the socket
        try {
            input = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        sock = socket;
    }
 
    public Socket getSocket() {
        return sock;
    }
 
    public byte[] receivePacket() throws IOException {
        // Receives an int telling how long of an array to expect
        int count = input.read();
 
        // Reads the byte array
        byte[] bytes = new byte[count];
        input.read(bytes);
 
        return bytes;
    }
 
    public void sendPacket(byte[] bytes) throws IOException {
        // Writes an int telling the length of the array to expect
        output.write(bytes.length);
        // Then writes the bytes
        output.write(bytes);
    }
}