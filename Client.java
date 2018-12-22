import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Client {
    private DatagramSocket client;

    public Client() throws SocketException {
        this.client = new DatagramSocket();
    }

    public void readFromFileAndSendToServer(){
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream("logs_BCS37_20181103_UTF-8.csv"), "UTF8"));

            String line;
            reader.readLine();

            int count = 0;

            while((line = reader.readLine()) != null) {
                String[] arr = line.split(",");
                FileLine fileLine = new FileLine();

                switch (arr.length){
                    case 8: fileLine.setIPAddress(arr[7]);
                    case 7: fileLine.setOrigin(arr[6]);
                    case 6: fileLine.setDescription(arr[5]);
                    case 5: fileLine.setEventName(arr[4]);
                    case 4: fileLine.setComponent(arr[3]);
                    case 3: fileLine.setEventContext(arr[2]);
                    case 2: {
                        String time = arr[0] + arr[1];
                        fileLine.setTime(time);
                        break;
                    }
                    case 1: fileLine.setTime(arr[1]); break;
                    case 0: break;
                    default: fileLine = new FileLine(arr[0] + arr[1], arr[2], arr[3], arr[4],
                            arr[5], arr[6], arr[7]);
                }

                //System.out.println(fileLine);

                sendFileLine(fileLine);

                count++;
            }

            System.out.println("count = " + count);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFileLine(FileLine fileLine) throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[4096], 4096,
                InetAddress.getByName("localhost"), 8888);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(fileLine);

        packet.setData(byteArrayOutputStream.toByteArray());
        client.send(packet);

        objectOutputStream.close();
        byteArrayOutputStream.close();
    }

    public static void main(String[] args) throws SocketException {
        new Client().readFromFileAndSendToServer();
    }
}
