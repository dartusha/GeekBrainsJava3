package ru.dartusha.chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatServer {
    public static final Logger logger = LogManager.getLogger(ChatServer.class.getName());

    private static final Pattern AUTH_PATTERN = Pattern.compile("^/auth (\\w+) (\\w+)$");
    ExecutorService executorService = Executors.newCachedThreadPool();

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

    public Map<User, ClientHandler> clientHandlerMap = Collections.synchronizedMap(new HashMap<>());

    private ThreadMaster threadMaster=new ThreadMaster();

    public static void main(String[] args) {
        logger.info("Start debug");
        ChatServer chatServer = new ChatServer();
        chatServer.start(Const.PORT);
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server started!");
            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream inp = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                BufferedReader inputCon = new BufferedReader(new InputStreamReader(System.in));
                logger.info("New client connected!");
               // startTimer(socket);
                try {
                    String authMessage = inp.readUTF();
                    logger.info(authMessage);
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
                            logger.info("Authorization for user "+username+" successful");
                            broadcastUserConnected(username);

                            threadMaster.addClientThread(inputCon,this);


                        } else {
                            logger.info("Authorization for user "+username+" failed");
                            out.writeUTF("/auth fails");
                            out.flush();
                            socket.close();
                           // threadMaster.startTimer(username,socket,clientHandlerMap);
                        }
                    } else {
                        logger.info("Incorrect authorization message: "+authMessage);
                        out.writeUTF("/auth fails");
                        out.flush();
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    public void sendMessage(String userFrom, User user, String msg) throws IOException {
        ClientHandler userToClientHandler = clientHandlerMap.get(user);
        if (userToClientHandler != null) {
            userToClientHandler.sendMessage(userFrom, msg,"");
        } else {
            logger.info("User "+user.login+" not found. Message from "+userFrom+" is lost.");
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
            logger.info("User "+userToClientHandler.getUsername()+" not found. Message from "+userFrom+" is lost.");
        }

    }

    public List<String> getUserList() {
        List<String> result = clientHandlerMap.keySet().stream()
                .map(user -> user.login)
                .collect(Collectors.toList());

        return result;
    }

    public void unsubscribeClient(User user) {
        clientHandlerMap.remove(user);
        try {
            broadcastUserDisconnected(user.login);
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
        logger.info("Refreshing usernames");
        //logger.info(user.login+","+newUsername);
        sendMessageAll("server", "Пользователь "+user.login+" изменил ник на "+newUsername);
        user.login=newUsername;
        authService.refresh();
        broadcastUserList();
    }
}


