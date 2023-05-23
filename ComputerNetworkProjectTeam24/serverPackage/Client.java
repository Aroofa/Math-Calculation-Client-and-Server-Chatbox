import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Client {

    private Socket s;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientName;
    static SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private Date startTime;

    public Client(Socket s, String clientName) {
        try {
            this.s = s;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.clientName = clientName;
        } catch (IOException exception) {
            closeEverything(s, bufferedReader, bufferedWriter);
        }

    }

    public void sendMessage() {
        try {
            bufferedWriter.write(clientName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            boolean done = false;
            while (!done) {
                String clientMessage = scanner.nextLine();
                if (clientMessage.equals("quit")) { 
                    done = true;
                    s.close();
                    closeEverything(s, bufferedReader, bufferedWriter);
                }
                else{
                    bufferedWriter.write(clientMessage);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
        } catch (IOException exception) {
            closeEverything(s, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromServer;
                while (!s.isClosed()) {
                    try {
                        msgFromServer = bufferedReader.readLine();
                        System.out.println(msgFromServer);
                    } catch (IOException exception) {
                        closeEverything(s, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket s, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
                bufferedReader.close();
                bufferedWriter.close();
        } catch (IOException exception) {
            System.out.println("Failed");
            exception.printStackTrace();
        }
    }

    public void setTime(Date startTime2){
        startTime = startTime2;
    }
    
    public static String calTime(Date start, Date end) {
		long differTime = end.getTime() - start.getTime();
		long differSec = (differTime / 1000) % 60;
		long differMin = (differTime / (1000 * 60)) % 60;
		long differHour = (differTime / (1000 * 60 * 60))% 24;
		return ("Hours: " + differHour + " Minutes: " + differMin + " Seconds: " + differSec);
	}

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in); 
        // when the client is connect to the server storing the start time
        Date startTime = new Date();
        System.out.println(formatter.format(startTime));
        System.out.println("Enter your name: ");
        String clientName = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, clientName);
        client.setTime(startTime);
        client.listenForMessage();
        client.sendMessage();
        
    }
}
