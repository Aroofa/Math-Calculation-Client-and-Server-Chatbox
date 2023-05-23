import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.net.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.PrintWriter;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket s;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientName;
    static SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private Date startTime;

    public ClientHandler(Socket s) {
        try {
            this.s = s;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.clientName = bufferedReader.readLine();
            this.startTime = new Date();

            File file = new File("clientTimeLog.txt");
            if (!file.exists()){
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(writer);
            //FileOutputStream outoutFile = new FileOutputStream(file, true); 
            //PrintWriter writer = new PrintWriter(file, "UTF-8");
            bw.write("\nClient: " + clientName + " StartTime: " + startTime);
            bw.close();

            clientHandlers.add(this);
            sendMessage("SERVER: " + clientName + " has joined the server!");

        } catch (IOException exception) {
            closeEverything(s, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String clientMessage;
        while (!s.isClosed()) {
            try {
                clientMessage = bufferedReader.readLine();
                sendMessage(clientMessage);
                if(clientMessage == null){
                    s.close();
                }
            } catch (IOException exception) {
                closeEverything(s, bufferedReader, bufferedWriter);
            }
        }
        closeEverything(s, bufferedReader, bufferedWriter);
    }


    public void sendMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                boolean equation = false;
                StringTokenizer tokenizer = new StringTokenizer(message);
                String firstOperand = "";
                String operator = "";
                String secondOperand = "";
                if(tokenizer.hasMoreTokens()){
                    firstOperand = ((tokenizer.nextToken()));
                }
                if(tokenizer.hasMoreTokens()){
                    operator = ((tokenizer.nextToken()));
                }
                if(tokenizer.hasMoreTokens()){
                    secondOperand = ((tokenizer.nextToken()));
                }

                String result = "Invalid expression";
                try{
                double num1 = Double.valueOf(firstOperand);
                double num2 = Double.valueOf(secondOperand);
                System.out.println(firstOperand);
                System.out.println(operator);
                System.out.println(secondOperand);
                switch(operator){
                    case "+": result = String.valueOf(num1+num2); break;
                    case "-": result = String.valueOf(num1-num2); break;
                    case "*": result = String.valueOf(num1*num2); break;
                    case "/": result = String.valueOf(num1/num2); break;
                }
                if(clientHandler.clientName.equals(clientName)){
                    equation = true;
                    clientHandler.bufferedWriter.write(result);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }} catch (NumberFormatException e) {
                }

                if (!clientHandler.clientName.equals(clientName) && !equation) {
                    clientHandler.bufferedWriter.write(clientName + ':' + message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (Exception exception) {
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        sendMessage("SERVER:" + clientName + " has left the server!");
    }

    public void closeEverything(Socket s, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            System.out.println(startTime);
            Date endTime = new Date();
            System.out.println(formatter.format(endTime)); 
            String timeResult = "";
            long differTime = endTime.getTime() - startTime.getTime();
            long differSec = (differTime / 1000) % 60;
            long differMin = (differTime / (1000 * 60)) % 60;
            long differHour = (differTime / (1000 * 60 * 60))% 24;
            timeResult = ("Hours: " + differHour + " Minutes: " + differMin + " Seconds: " + differSec);
            System.out.println(timeResult);
            
            // writing to the total time to a file 
            File file = new File("clientTimeLog.txt");
            if (!file.exists()){
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(writer);
            //FileOutputStream outoutFile = new FileOutputStream(file, true); 
            //PrintWriter writer = new PrintWriter(file, "UTF-8");
            bw.write("\nClient: " + clientName + " Endtime: " + endTime);
            bw.write("\nClient: " + clientName + " Time Spent: " + timeResult);
            bw.close();


            bufferedReader.close();
            bufferedWriter.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        removeClientHandler();
    }
}
