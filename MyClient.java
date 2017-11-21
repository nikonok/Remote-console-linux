import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client {
    private static int port = 49001;
    static String answer;

    public static void main(String[] args){
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), port);

            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());

            String command = "pwd";
            os.writeObject(command);
            ArrayList<String> ans = (ArrayList<String>) is.readObject();
            System.out.println("good");
            System.out.println(ans.toString());
//            os.writeObject("passWord");
//            ans = (ArrayList<String>) is.readObject();
//            System.out.println(ans.toString());


            //            String command = "sudo ls";
//            os.writeObject(command);
//            answer = (String) is.readObject();
//            System.out.println(answer);
//            os.writeObject("passWord");
//            answer = (String) is.readObject();
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
    }
}