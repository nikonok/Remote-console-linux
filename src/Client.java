import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private static int port = 49001;
    private static ArrayList<String> answer;

    public static void main(String[] args){
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), port);

            while (true) {
                ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
                Scanner sc = new Scanner(System.in);
                String command = sc.nextLine();
                os.writeObject(command);
                answer = (ArrayList<String>) is.readObject();
                if (!treatment(answer))
                    break;
            }

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
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean treatment(ArrayList<String> answer) {
        for (String s: answer) {
            String result = s.substring(0, 2);

            /** Success */
            if (Objects.equals(result, "10")) {
                System.out.println(s.substring(6));
                System.out.println("Ok");
            }
            /** Client Error */
            else if (Objects.equals(result, "20"))
                System.err.println("Bad request");
            else if (Objects.equals(result, "21")) {
                System.err.println("Unauthorized");
                System.out.print("Input password: ");
            }
            else if (Objects.equals(result, "22"))
                System.err.println("Not Found");
            /** Server Error */
            else if (Objects.equals(result, "38")) {
                System.err.println("Internal Server Error");
                return false;
            }
        }

        return true;
    }
}
