package ru.dartusha.chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private Map<String, ClientHandler> clientHandlerMap = Collections.synchronizedMap(new HashMap<>());

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
                        if (authService.authUser(username, password)) {
                            clientHandlerMap.put(username, new ClientHandler(username, socket, this));
                            out.writeUTF("/auth successful");
                            out.flush();
                            System.out.printf("Authorization for user %s successful%n", username);
                            broadcastUserConnected(username);

                            Thread serverMsg=
                                    new Thread(new Runnable() {

                                        @Override
                                        public void run() {
                                            while (true) {
                                                String inputStr = null;
                                                try {
                                                    inputStr = inputCon.readLine();
                                                        for(ClientHandler client : clientHandlerMap.values())
                                                        {
                                                            client.sendMessage("server",inputStr,"");
                                                        }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                System.out.format("Сообщение от сервера: %s",inputStr);
                                                System.out.println("");
                                            }
                                        }
                                    });
                            serverMsg.start();


                        } else {
                            //System.out.printf("Authorization for user %s failed%n", username);
                           //out.writeUTF("/auth fails");
                           // out.flush();
                           // socket.close();
                          //  startTimer(username, socket);
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

    public void sendMessage(String userFrom, String userTo, String msg) throws IOException {
        ClientHandler userToClientHandler = clientHandlerMap.get(userTo);
        if (userToClientHandler != null) {
            userToClientHandler.sendMessage(userFrom, msg,"");
        } else {
            System.out.printf("User %s not found. Message from %s is lost.%n", userTo, userFrom);
        }

    }

    public List<String> getUserList() {
        return new ArrayList<>(clientHandlerMap.keySet());
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

    public void broadcastUserConnected(String usrName) throws IOException {
        for(ClientHandler client : clientHandlerMap.values())
        {
            client.sendMessage("server", "User "+usrName+" connected","");
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


    public void broadcastUserDisconnected(String usrName) throws IOException {
        for(ClientHandler client : clientHandlerMap.values())
        {
            client.sendMessage("server", "User "+usrName+" disconnected","");
        }
    }

    public void startTimer(String userName, Socket socket){
        //test
        Thread timeoutThread=
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            System.out.printf("You have %d seconds for login.", Const.TIMEOUT);
                            System.out.println("");
                            Thread.sleep(1000*Const.TIMEOUT);
                            if (clientHandlerMap.get(userName)==null) {
                                socket.close();
                                System.out.printf("Authorization for user %s failed%n", userName);
                                System.out.println("");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });
        timeoutThread.start();
    }

    public void changeUsername(String oldUsername, String newUsername) throws SQLException, ClassNotFoundException {
        System.out.println("refreshing usernames");
        System.out.println(oldUsername+","+newUsername);
        ClientHandler clientHandler=clientHandlerMap.get(oldUsername);
        clientHandlerMap.put(newUsername, clientHandler);
        authService.refresh();

        /* TODO остается следующая проблема - если мы меняем username, то его нужно менять в clientHandlerMap и в clienthandler
        и других связанных. Проблема в том что username ключ, и его можно менять добавлением и удалением старого
        т.е. по сути нужно или "перезайти" клиентом. Или же выделять ник в отдельном поле
        */

      //  clientHandlerMap.remove(oldUsername);
    }
}


