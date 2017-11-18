import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    private static int port = 49001;
    private static ArrayList<ServerThread> users = new ArrayList<>();

    public static void main(String[] args){
        try{
            ServerSocket servsoc = new ServerSocket(port);
            ServerStopThread serverStopThread = new ServerStopThread();
            serverStopThread.start();
            System.out.println("Server initialized\n\n");

            while(true) {
                Socket socket = accept(servsoc);
                if(socket != null) {
                    System.out.println(socket.getInetAddress().getHostAddress() + " connected.");
                    ServerThread serverThread = new ServerThread(socket);
                    serverThread.start();
                    users.add(serverThread);
                }
            }
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
        finish();
    }

    private static Socket accept(ServerSocket serv){
        try{
            return serv.accept();
        }catch (IOException e){}
        return null;
    }

    private static void serverIsDown(){
        for(ServerThread s : users){
            s.serverIsDownMessage();
        }
    }

    public synchronized static void finish(){
        serverIsDown();
        System.out.println("Server Is Down\n");
        System.exit(0);
    }
}

//1* - Success
//    10 - OK                       #обрабатываешь дальше
//2* - Client Error
//    20 - Bad Request              #передан был не String, а хер знает что (ClassNotFoundException)
//    21 - Unauthorized             #введите пароль пожалуйста (пусть вводят слово passWord, или сам придумай)
//    22 - Not Found                #неверная команда
//3* - Server Error
//    30 - Internal Server Error    #внутренняя ошибка сервера(пояснения по ней будут в строке)
//    31 - Server is down         #сообщение о выключении сервера

class ServerThread extends Thread{
    private static int uniqID = 0;
    private int ID;
    private int timeOut = 10000;

    private ObjectOutputStream os;
    private ObjectInputStream is;
    private InetAddress address;

    private String answer10 = "10 OK";
    private String answer20 = "20 Not string!";
    private String answer21 = "21 Unauthorized";
    private String answer22 = "22 Command not found";
    private String answer30 = "30 Internal Server Error ";
    private String answer31 = "31 Server is down";

    private String password = "echo \"nikonok\" | ";
    private String userPassword = "passWord";
    private String fileName = "shellfile_";

    ServerThread(Socket sock) throws IOException{
        this.setDaemon(true);
        sock.setSoTimeout(timeOut);
        os = new ObjectOutputStream(sock.getOutputStream());
        is = new ObjectInputStream(sock.getInputStream());
        address = sock.getInetAddress();
        ID = uniqID++;
        fileName += Integer.toString(ID);
    }

    public void run(){
        ArrayList<String> answer;
        try {
            String command;
            Process p;
            Runtime runTime = Runtime.getRuntime();

            while (!(command = (String) is.readObject()).equals("exit")) {
                if (command.equals("sudo exit")) {
                    int auth;
                    answer = new ArrayList<>();
                    answer.add(answer21);
                    os.writeObject(answer);
                    while ((auth = authorize((String) is.readObject())) == 0 || auth == -1) {
                        if(auth == -1)
                            throw new IOException();
                        System.out.println("\n" + address.getHostAddress() + " " + answer21 + "\n");
                        os.writeObject((new ArrayList<String>()).add(answer21));
                    }
                    break;
                } else if (isSudo(command)) {
                    int auth;
                    answer = new ArrayList<>();
                    answer.add(answer21);
                    os.writeObject(answer);
                    while ((auth = authorize((String) is.readObject())) == 0 || auth == -1) {
                        if(auth == -1)
                            throw new IOException();
                        System.out.println("\n" + address.getHostAddress() + " " + answer21 + "\n");
                        os.writeObject((new ArrayList<String>()).add(answer21));
                    }
                    if(command.substring(0,4).equals("sudo")) {
                        if (!command.substring(0, 7).equals("sudo -S")) {
                            command = command.substring(0, 4) + " -S " + command.substring(4);
                        }
                    } else {
                        command = "sudo -S " + command;
                    }
                    command = password + command;
                }
                System.out.println("\n" + address.getHostAddress() + "\n" + "Executing: \n\t" + command + "\n");
                answer = new ArrayList<>();
                writeCommand(command);
                try {
                    p = runTime.exec("sh " + fileName);
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    String tmp;
                    while ((tmp = br.readLine()) != null) {
                        answer.add(tmp);
                    }
                    answer.add(0, answer10);
                }catch (IOException e){
                    answer.add(answer22);
                }
                os.writeObject(answer);
                if(answer.get(0).equals(answer22))
                    System.out.println("\n" + address.getHostAddress() + ":\n  " + answer22 + "\n");
                else
                    System.out.println("\n" + address.getHostAddress() + ":\n  " + answer10 + "\n");
            }
        }catch (ClassNotFoundException e){
            try {
                answer = new ArrayList<>();
                answer.add(answer20);
                os.writeObject(answer);
                System.out.println("\n" + address.getHostName() + ":\n  " + answer20 + "\n");
            }catch (IOException er){
                System.out.println("Problems with socket. Disconnect.");
                er.printStackTrace();
            }
        }catch (IOException e){
            System.out.println(address.getHostAddress() + " disconnecting...");
        }catch (Exception e){
            try {
                answer = new ArrayList<>();
                answer.add(answer30);
                os.writeObject(answer);
                System.out.println("\n" + address.getHostName() + ":\n  " + answer30 + "\n");
            }catch (IOException er){
                System.out.println("Problems with socket. Disconnect.");
                er.printStackTrace();
            }
        }
        finally {
            disconnect();
        }
    }

    public void serverIsDownMessage(){
        try {
            ArrayList<String> answer = new ArrayList<>();
            answer.add(answer31);
            os.writeObject(answer);
            System.out.println("\n" + address.getHostName() + ":\n  " + answer30 + "\n");
        }catch (IOException e){}
    }

    public void disconnect(){
        try{
            System.out.println("Disconnect from " + address.getHostName());
            os.close();
            is.close();
        }catch (IOException e){
            this.interrupt();
        }finally {
            deleteCommand();
        }
    }

    private int authorize(String str){
        if(str.equals("exit"))
            return -1;
        else if(str.equals(userPassword))
            return 1;
        else
            return 0;
    }

    private boolean isSudo(String command){
        return command.indexOf("sudo") > 0;
    }

    private void writeCommand(String command) throws FileNotFoundException {
        File file = new File(fileName);
        PrintStream print = new PrintStream(file.getAbsoluteFile());
        print.println(command);
        print.flush();
        print.close();
    }
    private void deleteCommand(){
        File file = new File(fileName);
        file.delete();
    }
}

class ServerStopThread extends Thread{
    static final String shutdown = "shutdown";
    private Scanner fin;

    public ServerStopThread(){
        this.setDaemon(true);
        fin = new Scanner(System.in);
        System.out.println("Enter '" + shutdown + "' to stop the server");
    }

    public void run(){
        while (true){
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                break;
            }
            if(!fin.hasNextLine())
                continue;
            String str = fin.nextLine();
            if(str.equals(shutdown)){
                quit();
                break;
            }
        }
    }

    private void quit(){
        System.out.println("Server is shutting down...");
        fin.close();
        Server.finish();
    }
}
