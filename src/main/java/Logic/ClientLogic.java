package Logic;

import problem.Client;
import problem.Main;
import problem2.JSON.JSONFormatterImpl;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ClientLogic {
    private static final String usersPath = "src/main/java/problem/users";

    public static String createClient(String request){
        Map<String,String> clientParams = new HashMap<>(stringProcessing(request));
        String name = clientParams.get("name");
        int age = Integer.parseInt(clientParams.get("age"));
        double salary = Double.parseDouble(clientParams.get("salary").replace(",", "."));
        return saveClient(new Client(getNewId(), name, age, salary));
    }

    public static String deleteClient(String id){
        File dir = new File(usersPath+"/"+id+".bin");
        if(dir.exists()){
            if(dir.delete()){
                return Main.answerOK+"client is deleted\n";
            }
        }
        return Main.answer404+"Client is not exist";
    }

    public static String getClient(String id){
        File dir = new File(usersPath+"/"+id+".bin");
        if(dir.exists()){
                StringBuilder sb = new StringBuilder();
                sb.append(Main.answerOK);
                try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dir))){
                    Client client = (Client)ois.readObject();
                    JSONFormatterImpl json = new JSONFormatterImpl();
                    sb.append(json.marshall(client)).append("<br/>");
                }catch(Exception e){
                    e.printStackTrace();
                }
                return sb.toString();
        }
        return Main.answer404+"Client is not exist";
    }

    public static String getClientsList(){
        File dir = new File(usersPath);
        StringBuilder sb = new StringBuilder();
        sb.append(Main.answerOK);
        for(File f : dir.listFiles()){
            try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(usersPath+"/"+f.getName()))){
                Client client = (Client)ois.readObject();
                JSONFormatterImpl json = new JSONFormatterImpl();
                sb.append(json.marshall(client)).append("<br/>");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static int getNewId(){
        File dir = new File(usersPath);
        int max = 0;
        for(File f : dir.listFiles()){
            int id = Integer.valueOf(f.getName().substring(0, f.getName().indexOf(".")));
            if(id > max) max = id;
        }
        return max+1;
    }

    private static String saveClient(Client client){
        StringBuilder answer = new StringBuilder();
        answer
                .append(Main.answerOK)
                .append("id:").append(client.getId()).append("\n");
        try (ObjectOutputStream oos = new ObjectOutputStream
                (new FileOutputStream(usersPath + "/" + client.getId() + ".bin"))) {
            oos.writeObject(client);
        }catch(Exception e){e.printStackTrace();}
        return answer.toString();
    }

    private static Map<String, String> stringProcessing (String request){
        Map<String, String> paramList = new HashMap<>();
        String param = request.substring(request.indexOf("?")+1);
        String [] paramsArr = param.split("&");
        for (String i : paramsArr){
            paramList.put(i.substring(0, i.indexOf("=")), i.substring(i.indexOf("=")+1, i.length()));
        }
        return paramList;
    }
}