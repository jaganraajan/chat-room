import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private static final int PORT = 8080;
    private static HashMap<String, PrintWriter> connectedClients = new HashMap<>();
    private static final int MAX_CONNECTED = 100;
    private static ServerSocket server;

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String name;

        public ClientHandler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Client connected: " + socket.getInetAddress());
            try{
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Enter your username");

                name = in.readLine();
                if(name == null) return;

                out.println("Welcome to the chatroom " + name);

                System.out.println(name + " has joined the chatroom");

                broadcastToALlClients("[System] " + name + " has joined the chatroom!");

                // add client name and corresponding PrintWriter to hashmap
                connectedClients.put(name, out);

                while(true){
                    String message = in.readLine();
                    if(message == null || message.toLowerCase().equals("--quit")) break;
                    else{
                        broadcastToALlClients(name + ": " + message);
                    }
                }
            }
            catch (IOException e){
                System.err.println(e.getStackTrace());
            }
            finally {
                if(name != null) {
                    System.out.println(name + " is leaving");
                    connectedClients.remove(name);
                    broadcastToALlClients(name + " left the chatroom");
                }
            }
        }

    }

    public static void broadcastToALlClients(String message){
        // Write to all the connected clients PrintWriter
        for(PrintWriter p: connectedClients.values()){
            p.println(message);
        }
    }

    public static void main(String[] args) throws IOException {
        try{
            server = new ServerSocket(PORT);
            System.out.println("[Server] Waiting for client connection");

            // create new thread for each client connection
            while(true) {
                if(connectedClients.size() < MAX_CONNECTED){
                    Thread newClient = new Thread(new ClientHandler(server.accept()));
                    newClient.start();
                }
            }
        }
        finally {
            server.close();
        }


    }

}
