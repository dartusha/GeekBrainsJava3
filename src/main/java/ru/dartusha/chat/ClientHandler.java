package ru.dartusha.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ClientHandler {

   // private final Thread handleThread;
    private final DataInputStream inp;
    private final DataOutputStream out;
    private final ChatServer server;
    private User user;
    private final Socket socket;
    private final Future futureThread;

    ExecutorService executorService = Executors.newCachedThreadPool();

    public ClientHandler(final User user, final Socket socket, final ChatServer server) throws IOException {
        this.user = user;
        this.socket = socket;
        this.server = server;
        this.inp = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.futureThread = executorService.submit(() -> {
            try {
                boolean flag=true;
                while (flag)//(Thread.currentThread().isInterrupted()
                    //)
                {
                        String msg = inp.readUTF();
                    System.out.printf("Message from user %s: %s%n", user.login, msg);
                    if (msg.contains("$GET_USERS")) {
                        List<String> str = server.getUserList();
                        String sendMsg = "$USERS:";
                        for (String st : str) {
                            sendMsg += st + ",";
                        }
                        //  server.sendMessage("server",user,sendMsg);
                        server.sendServerMessageAll("server", sendMsg);
                    }
                    if (msg.equals(Const.CMD_CLOSED)) {
                        System.out.format("Client %s disconnected", user.login);
                        inp.close();
                        out.close();
                        server.unsubscribeClient(user);
                        socket.close();
                        flag=false;
                    }

                    if (msg.contains(Const.CMD_CHANGE_NAME)) {
                        server.changeUsername(user, msg.substring(Const.CMD_CHANGE_NAME.length(), msg.length()));
                    }

                    Matcher matcher = Const.MESSAGE_PATTERN.matcher(msg);
                    if (matcher.matches()) {
                        String userTo = matcher.group(1);
                        String sendMsg = matcher.group(2);
                        server.sendMessage(user.login, userTo, sendMsg);
                    } else {
                        server.sendMessageAll(user.login, msg);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
               // System.out.printf("Client %s disconnected%n", user.login);
               // try {
                //    socket.close();
                   // server.unsubscribeClient(user.login);
               // } catch (IOException e) {
               //     e.printStackTrace();
               // }
            }

        });
     //   try {
     //       System.out.println(this.futureThread.get());
      //  } catch (InterruptedException e) {
      //      e.printStackTrace();
       // } catch (ExecutionException e) {
        //    e.printStackTrace();
        //}
    }

    public void sendMessage(String userTo, String msg, String userFrom) throws IOException {
       // System.out.println("string:"+String.format(Const.MESSAGE_SEND_PATTERN, userTo, msg));
        if (msg.contains("/a")){
            out.writeUTF(String.format(Const.MESSAGE_SEND_ALL_PATTERN, userFrom, msg.replace("/a","")));
        }
        else{
            out.writeUTF(String.format(Const.MESSAGE_SEND_PATTERN, userTo, msg));
        }
    }

    public String getUsername() {
        return user.login;
    }

    public Socket getSocket(String username) {
        return socket;
    }

}