import java.io.*;
import java.net.*;

public class Server {
    private static int port = 49001;

    public static void main(String[] args){
        try{
            ServerSocket servsoc = new ServerSocket(port);
            System.out.println("Server initialized");

            while(true) {
                Socket socket = servsoc.accept();
                System.out.println(socket.getInetAddress().getHostAddress() + " connected.");

                ServerThread servthread = new ServerThread(socket);
                servthread.start();
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}

class ServerThread extends Thread{
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private InetAddress address;

    public ServerThread(Socket sock) throws IOException{
        os = new ObjectOutputStream(sock.getOutputStream());
        is = new ObjectInputStream(sock.getInputStream());
        address = sock.getInetAddress();
    }

    public void run(){
        try {
            String str = (String) is.readObject();

            Process p = Runtime.getRuntime().exec(str);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String ans = br.readLine();

            os.writeObject(ans);
            System.out.println(ans);

        }catch (IOException | ClassNotFoundException e){
            System.out.println("Disconnect");
            e.printStackTrace();
        }finally {
            disconnect();
        }
    }

    public void disconnect(){
        try{
            System.out.println("Disconnect from " + address.getHostName());
            os.close();
            is.close();
        }catch (IOException e){
            this.interrupt();
        }
    }

}
