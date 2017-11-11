import java.io.*;
import java.net.*;

public class Client {
    private static int port = 49001;

    public static void main(String[] args){
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), port);
            PrintStream ps = new PrintStream(socket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            for(int i = 0; i < 10; i++){
//                ps.print("PING");
//                //System.out.println("PING + " + Integer.toString(br.read()));
//                Thread.sleep(1000);
//            }
            ps.println("pwd");
            socket.close();
        }
        catch (UnknownHostException e){
            System.out.println("Address no responsable.");
            e.printStackTrace();
        }
        catch (IOException e){
            System.out.println("Exception on IO.");
            e.printStackTrace();
        }
//        catch (InterruptedException e){
//            System.out.println("Exception on thread.");
//            e.printStackTrace();
//        }
    }
}
