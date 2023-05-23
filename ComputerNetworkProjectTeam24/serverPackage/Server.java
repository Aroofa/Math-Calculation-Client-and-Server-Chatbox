import java.io.IOException;
import java.net.*;

public class Server {
    private ServerSocket mySocket;

    public Server(ServerSocket mySocket) {
        this.mySocket = mySocket;
    }

    public void startServer() {
        try {
            while (mySocket.isClosed() == false) {
                Socket s = mySocket.accept();
                System.out.println("A new user has connected to the server!");
                ClientHandler clientHandler = new ClientHandler(s);

                Thread myThread = new Thread(clientHandler);
                myThread.start();
            }
        } catch (IOException exception) {

        }
    }

    public void closeServer() {
        try {
            if (mySocket != null) {
                mySocket.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket mySocket = new ServerSocket(1234);
        Server server = new Server(mySocket);
        server.startServer();
    }
}