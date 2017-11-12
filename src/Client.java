import java.io.*;
import java.net.*;

public class Client {
    private static int port = 49001;
    static String answer;

    public static void main(String[] args){
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), port);

            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            String command = "sudo pwd";
            os.writeObject(command);
            answer = (String) is.readObject();
            System.out.println(answer);
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
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
//        catch (InterruptedException e){
//            System.out.println("Exception on thread.");
//            e.printStackTrace();
//        }
    }
}
