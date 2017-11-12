import java.io.*;
import java.net.*;

public class Client {
    private static int port = 49001;
    static String answer;

    public static void main(String[] args){
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), port);

            while (true) {
                ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
                String command = "sudo pwd";
                os.writeObject(command);
                answer = (String) is.readObject();
                String result = answer.substring(0, 2);
                /** Success */
                if (result == "10") {
                    System.out.println(answer.substring(6));
                    System.out.println("Ok");
                }
                /** Client Error */
                else if (result == "20")
                    System.err.println("Bad request");
                else if (result == "21")
                    System.err.println("Unauthorized");
                else if (result == "22")
                    System.err.println("Not Found");
                /** Server Error */
                else if (result == "38")
                    System.err.println("Internal Server Error");

                socket.close();
            }
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
