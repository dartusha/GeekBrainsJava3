package ru.dartusha.chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatServer {

    private static final Pattern AUTH_PATTERN = Pattern.compile("^/auth (\\w+) (\\w+)$");

    private AuthService authService;

    {
        try {
            authService = new AuthServiceImpl();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Map<User, ClientHandler> clientHandlerMap = Collections.synchronizedMap(new HashMap<>());

    private ThreadMaster threadMaster=new ThreadMaster();

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start(Const.PORT);
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started!");
            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream inp = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                BufferedReader inputCon = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("New client connected!");
               // startTimer(socket);
                try {
                    String authMessage = inp.readUTF();
                    System.out.println(authMessage);
                    Matcher matcher = AUTH_PATTERN.matcher(authMessage);
                    if (matcher.matches()) {
                        String username = matcher.group(1);
                        String password = matcher.group(2);
                        Integer userId=0;
                        if (authService.authUser(username, password,userId)) {
                            User user = new User(userId, username, password);
                            clientHandlerMap.put(user, new ClientHandler(user, socket, this));
                            out.writeUTF("/auth successful");
                            out.flush();
                            System.out.printf("Authorization for user %s successful%n", username);
                            broadcastUserConnected(username);

                            threadMaster.addClientThread(inputCon,clientHandlerMap);


                        } else {
                            System.out.printf("Authorization for user %s failed%n", username);
                            out.writeUTF("/auth fails");
                            out.flush();
                            socket.close();
                           // threadMaster.startTimer(username,socket,clientHandlerMap);
                        }
                    } else {
                        System.out.printf("Incorrect authorization message %s%n", authMessage);
                        out.writeUTF("/auth fails");
                        out.flush();
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String userFrom, User user, String msg) throws IOException {
        ClientHandler userToClientHandler = clientHandlerMap.get(user);
        if (userToClientHandler != null) {
            userToClientHandler.sendMessage(userFrom, msg,"");
        } else {
            System.out.printf("User %s not found. Message from %s is lost.%n", user.login, userFrom);
        }

    }

    public void sendMessage(String userFrom, String userTo, String msg) throws IOException {
        ClientHandler userToClientHandler=null;
        for(ClientHandler client : clientHandlerMap.values())
        {
           if (client.getUsername().equals(userTo))
               userToClientHandler=client;
        }

        if (userToClientHandler != null) {
            userToClientHandler.sendMessage(userFrom, msg,"");
        } else {
            System.out.printf("User %s not found. Message from %s is lost.%n", userToClientHandler.getUsername(), userFrom);
        }

    }

    public List<String> getUserList() {
        List<String> result = clientHandlerMap.keySet().stream()
                .map(user -> user.login)
                .collect(Collectors.toList());

        for (String rec:
                result) {
            System.out.println("test:"+rec);
        }

        return result;
    }

    public void unsubscribeClient(ClientHandler clientHandler) {
        String userName=clientHandler.getUsername();
        clientHandlerMap.remove(userName);
        try {
            broadcastUserDisconnected(userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribeClient(String userName) {
        clientHandlerMap.remove(userName);
        try {
            broadcastUserDisconnected(userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void broadcastUserList() throws IOException {
        List<String> str= getUserList();
        String sendMsg="$USERS:";

        for (String st:str){
            sendMsg+=st+",";
        }

        for(ClientHandler client : clientHandlerMap.values())
        {
            client.sendMessage("server",sendMsg,"");
        }
    }


    public void broadcastUserConnected(String usrName) throws IOException {
        List<String> str= getUserList();
        String sendMsg="$USERS:";

        for (String st:str){
            sendMsg+=st+",";
        }

        for(ClientHandler client : clientHandlerMap.values())
        {
            client.sendMessage("server", "User "+usrName+" connected","");
            client.sendMessage("server",sendMsg,"");
        }
    }

    public void sendMessageAll(String userFrom, String msg) throws IOException {
        for(ClientHandler client : clientHandlerMap.values())
        {
           if (!client.getUsername().equals(userFrom)) {
               client.sendMessage(client.getUsername(), "/a " + msg,userFrom);
           }
        }
    }

    public void sendServerMessageAll(String userFrom, String msg) throws IOException {
        for(ClientHandler client : clientHandlerMap.values())
        {
            if (!client.getUsername().equals(userFrom)) {
                client.sendMessage(client.getUsername(), msg,userFrom);
            }
        }
    }


    public void broadcastUserDisconnected(String usrName) throws IOException {
        for(ClientHandler client : clientHandlerMap.values())
        {
            client.sendMessage("server", "User "+usrName+" disconnected","");
        }
    }


    public void changeUsername(User user, String newUsername) throws SQLException, ClassNotFoundException, IOException {
        System.out.println("refreshing usernames");
        System.out.println(user.login+","+newUsername);
        sendMessageAll("server", "Пользователь "+user.login+" изменил ник на "+newUsername);
        user.login=newUsername;
        authService.refresh();
        broadcastUserList();
    }
}


