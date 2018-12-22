import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server extends Thread{
    private DatagramSocket server;

    public Server(int port) throws SocketException {
        this.server = new DatagramSocket(port);
    }

    public Server() throws SocketException {
        this(8888);
    }

    public void run(){
        int counter = 0;
        while(true){
            byte[] buffer = new byte[4096];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);

            try {
                server.receive(request);

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(request.getData());
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

                FileLine fileLine = (FileLine) objectInputStream.readObject();

                System.out.println(fileLine);
                counter++;
                System.out.println("counter = " + counter);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = null;
        try {
            server = new Server();
            server.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
