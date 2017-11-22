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
        String s = answer.get(0);
        String numberAnswer = s.substring(0, 2);

        /** Client Error */
        if (Objects.equals(numberAnswer, "20")) {
            System.err.println("Bad request");
            return true;
        }
        else if (Objects.equals(numberAnswer, "21")) {
            System.err.println("Unauthorized");
            System.out.print("Input password: ");
            return true;
        }
        else if (Objects.equals(numberAnswer, "22")) {
            System.err.println("Not Found");
            return true;
        }
        /** Server Error */
        else if (Objects.equals(numberAnswer, "38")) {
            System.err.println("Internal Server Error");
            return false;
        }
        answer.remove(0);

        /** Success */
        for (String result: answer)
            System.out.println(result);
        return true;
    }
}
