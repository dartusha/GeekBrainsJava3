package ru.dartusha.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ClientHandler {

    private final Thread handleThread;
    private final DataInputStream inp;
    private final DataOutputStream out;
    private final ChatServer server;
    private final String username;
    private final Socket socket;

    public ClientHandler(final String username, final Socket socket, final ChatServer server) throws IOException {
        this.username = username;
        this.socket = socket;
        this.server = server;
        this.inp = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        this.handleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        String msg = inp.readUTF();
                        System.out.printf("Message from user %s: %s%n", username, msg);
                        if (msg.contains("$GET_USERS")){
                            List<String> str= server.getUserList();
                            String sendMsg="$USERS:";
                            for (String st:str){
                                sendMsg+=st+",";
                            }
                            server.sendMessage("server",username,sendMsg);
                        }
                        if (msg.equals(Const.CMD_CLOSED)){
                            System.out.format("Client %s disconnected", username);
                            inp.close();
                            out.close();
                            server.unsubscribeClient(username);
                            socket.close();
                        }

                        if (msg.contains(Const.CMD_CHANGE_NAME)){
                           // System.out.format("Client %s disconnected", username);
                          //  inp.close();
                           // out.close();
                            server.changeUsername(username,msg.substring(Const.CMD_CHANGE_NAME.length(),msg.length()));
                           // socket.close();
                        }

                        Matcher matcher = Const.MESSAGE_PATTERN.matcher(msg);
                        if (matcher.matches()) {
                            String userTo = matcher.group(1);
                            String sendMsg= matcher.group(2);
                            server.sendMessage(username,userTo,sendMsg);
                        }
                        else{
                            server.sendMessageAll(username,msg);
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    System.out.printf("Client %s disconnected%n", username);
                    try {
                        socket.close();
                      //  server.broadcastUserDisconnected(username);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        handleThread.start();
    }

   // public DataOutputStream getOut() {
  //      return out;
  // }

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
        return username;
    }

    public Socket getSocket(String username) {
        return socket;
    }

}