import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8080;
    private static Socket clientSocket;

    private static class Listener implements Runnable{
        BufferedReader in;
        @Override
        public void run() {
            try{
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String read;

                // print received text from the server
                while(true){
                    read = in.readLine();
                    if(read != null && !read.isEmpty()) System.out.println(read);
                }
            }
            catch (Exception e) {
                System.out.println(e.getStackTrace());
            }

        }
    }

    private static class Writer implements Runnable{
        PrintWriter out;

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                //send input text to the server
                while(true){
                    if(scanner.hasNext()){
                        out.println(scanner.nextLine());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                scanner.close();
            }
        }
    }

    public static void main(String[] args) {
        try {
            clientSocket = new Socket(SERVER_IP, SERVER_PORT);
        }
        catch (Exception e){
            System.err.println(e.getStackTrace());
        }

        // Perform Read and Write  in separate threads for non-blocking operations
        new Thread(new Writer()).start();
        new Thread(new Listener()).start();

    }
}
