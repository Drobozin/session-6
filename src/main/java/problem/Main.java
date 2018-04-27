package problem;

import Logic.ClientLogic;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Main {
    public static final String answerOK = "HTTP/1.0 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n";
    public static final String answer404 = "HTTP/1.0 404 Not found\r\n" + "Content-Type: text/html\r\n" + "\r\n";
    private static final String propPath = "src/main/java/problem/config/prop";
    private static final String commandCreate = "/user/create?";
    private static final String commandDelete = "/user/delete/";
    private static final String commandList = "/user/list/";
    private static final String commandGetUser = "/user/";

    public static void main(String[] arg){
        try {
            Properties property = new Properties();
            property.load(new FileInputStream(propPath));
            int port = Integer.parseInt(property.getProperty("http.port"));
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is waiting for connection");

            while(true) {
                Socket client = serverSocket.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                String request = br.readLine();
                String answer = "";
                assert request != null;
                String [] reqArr = request.split(" ");
                    for (String iReqArr : reqArr) {
                        if (iReqArr.startsWith(commandCreate) ) {
                            answer = ClientLogic.createClient(iReqArr);
                        }
                        else if (iReqArr.startsWith(commandDelete) ) {
                            int start = iReqArr.lastIndexOf('/')+1;
                            String id = iReqArr.substring(start, iReqArr.length());
                            answer = ClientLogic.deleteClient(id);
                        }
                        else if(request.contains(commandList)) {
                            answer = ClientLogic.getClientsList();
                        }
                        else if (iReqArr.startsWith(commandGetUser) ) {
                            int start = iReqArr.lastIndexOf('/')+1;
                            String id = iReqArr.substring(start, iReqArr.length());
                            answer = ClientLogic.getClient(id);
                        }
                    }
                out.write(answer);
                out.flush();
                out.close();
                br.close();

                if ( request.contains("quit") )
                    break;
            }
            System.out.println("Server is stopped");

        }catch(Exception ignored){

        }
    }
}
