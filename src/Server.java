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

                ServerThread serthread = new ServerThread(socket);
                serthread.run();
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}

class ServerThread extends Thread{
    private PrintStream os;
    private BufferedReader is;
    private InetAddress address;

    public ServerThread(Socket sock) throws IOException{
        os = new PrintStream(sock.getOutputStream());
        InputStream input = sock.getInputStream();
        is = new BufferedReader(new InputStreamReader(input));
        address = sock.getInetAddress();
    }

    public void run(){
        try{
            int i = 0;
            String str;
            while((str = is.readLine()) != null){
                System.out.println(str);

                Process p = Runtime.getRuntime().exec(str);
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                System.out.println(br.readLine());

               //i++;
               //System.out.println("Get answer! i=" + Integer.toString(i) + " Hostname = " + address.getHostName());
               os.println(i);
            }
        }catch (IOException e){
            System.out.println("Disconnect");
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
