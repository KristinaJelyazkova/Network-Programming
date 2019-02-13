import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

public class Server extends Thread{
    private DatagramSocket server;

    public Server(int port) throws SocketException {
        this.server = new DatagramSocket(port);
    }

    public Server() throws SocketException {
        this(8888);
    }

    public void run(){
        Map<Pair, ArrayList<FileLine>> clientFiles = new HashMap<>();

        while(true){

            byte[] buffer = new byte[4096];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);

            try {
                server.receive(request);
                InetAddress inetAddress = request.getAddress();
                Integer port = request.getPort();
                Pair pair = new Pair(inetAddress, port);

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(request.getData());
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

                FileLine fileLine = (FileLine) objectInputStream.readObject();

                //if the packet received marks the end of the file receiving
                if(fileLine.getTime().equals("end")){
                    String name = inetAddress.toString().substring(1) + "_" + port + ".txt";
                    String fileName = "input_" + name;
                    String resultFileName = "result_" + name;

                    /* create the input file for the algorithm in the needed format and return what each integer means
                    in the result file - a concrete event name*/
                    Map<Integer, String> eventNameIdsReversed = makeInputFile(fileName, clientFiles.get(pair));

                    // apply algorithm and write results in file with name resultFileName (result_<ip_addr>_<port>.txt)
                    File outputFile = new File(resultFileName);
                    (new AlgoApriori()).runAlgorithm(0.005, fileName, resultFileName);

                    // send result file as readable by human information
                    sendResult(inetAddress, port, resultFileName, eventNameIdsReversed);
                }
                else { // add the file line to the list in the map
                    ArrayList<FileLine> linesList = clientFiles.get(pair);

                    // if list does not exist create it
                    if(linesList == null) {
                        linesList = new ArrayList<FileLine>();
                    }
                        linesList.add(fileLine);
                        clientFiles.put(pair, linesList);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<Integer, String> makeInputFile(String fileName, ArrayList<FileLine> fileLines) throws IOException {
        int eventNameId = 1;
        Map<String, Integer> eventNameIds = new HashMap<>();
        Map<Integer, String>  eventNameIdsReversed = new HashMap<>();
        Map<String, TreeSet<Integer>> inputFileLine = new HashMap<>();

        for (int i = 0; i < fileLines.size(); i++) {
            FileLine fileLine = fileLines.get(i);
            String eventName = fileLine.getEventName();

            if(!eventNameIds.containsKey(eventName)){
                eventNameIds.put(eventName, eventNameId);
                eventNameIdsReversed.put(eventNameId, eventName);
                eventNameId++;
            }

            String date = fileLine.getTime().split(" ")[0];
            TreeSet<Integer> itemSet = inputFileLine.get(date);

            // if list does not exist create it
            if(itemSet == null) {
                itemSet = new TreeSet<>();
            }
                itemSet.add(eventNameIds.get(eventName));
                inputFileLine.put(date, itemSet);
        }

        // write to file:
        File inputFile = new File(fileName);
        FileOutputStream fos = new FileOutputStream(inputFile);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        inputFileLine.forEach((date, itemset) -> {
            StringBuilder line = new StringBuilder();
            Iterator<Integer> it = itemset.iterator();
            boolean first = true;

            while(it.hasNext()){
                if(first) {
                    line.append(it.next());
                    first = false;
                }
                else{
                    line.append(" " + it.next());
                }
            }

            try {
                bw.write(line.toString());
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bw.close();

        return eventNameIdsReversed;
    }


    private void sendResult(InetAddress inetAddress, Integer port, String resultFileName,
                            Map<Integer, String> eventNameIdsReversed) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(resultFileName)));
        String line;

        while((line = reader.readLine()) != null){
            String[] arr = line.split(" ");
            int index = 0;
            StringBuilder resultLine = new StringBuilder();
            resultLine.append("The set of events {");

            while(index < arr.length){
                String eventName = eventNameIdsReversed.get(Integer.parseInt(arr[index]));
                resultLine.append(eventName);

                index++;

                if(index < arr.length && arr[index].equals("")){
                    resultLine.append("} occurred in ");
                    index += 2;
                    break;
                }
                else if(index < arr.length && arr[index].equals("#SUP:")){
                    resultLine.append("} occurred in ");
                    index++;
                    break;
                }
                else{
                    resultLine.append(", ");
                }
            }

            if(index < arr.length){
                resultLine.append(Integer.parseInt(arr[index]) + " days.");
            }

            //System.out.println(resultLine.toString().getBytes().length);
            int size = resultLine.toString().getBytes().length;

            DatagramPacket response = new DatagramPacket(new byte[size], size);
            response.setAddress(inetAddress);
            response.setPort(port);

            response.setData(resultLine.toString().getBytes());
            server.send(response);
        }

        //System.out.println("Sent.");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DatagramPacket response = new DatagramPacket(new byte[5], 5);
        response.setAddress(inetAddress);
        response.setPort(port);

        response.setData("END".getBytes());
        server.send(response);
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
