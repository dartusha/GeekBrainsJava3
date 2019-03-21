package ru.dartusha.chat;

import javafx.application.Platform;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Network implements Closeable {

    private static final String AUTH_PATTERN = "/auth %s %s";

    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;
    private final MessageSender messageSender;
 //   private final Thread receiver;
    private  Future receiverFuture;

    private  String usr;

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    public Network(String hostName, int port, MessageSender messageSender) throws IOException {
        this.socket = new Socket(hostName, port);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
        this.messageSender = messageSender;

     //   this.receiver = createReceiverThread();
    }

    private Future createReceiverFuture() {
        Future future = executorService.submit(()-> {
            boolean flag=true;
            while (flag) {
                try {
                    String text = in.readUTF();
                    Platform.runLater(new Runnable() { //TODO Вот тут немного непонятно как заменить на executorService
                        @Override
                        public void run() {
                            System.out.println("New message:" + text);
                            Message msg = new Message("server", usr, text);
                            messageSender.submitMessage(msg);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    flag=false;
                }
            }
        });
        return future;
    }


    public void sendMessageToUser(Message message) {
        sendMessage(message.getText());
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authorize(String username, String password) {
        try {
            out.writeUTF(String.format(AUTH_PATTERN, username, password));
            String response = in.readUTF();
            if (response.equals("/auth successful")) {
                usr = username;
                System.out.println("username:"+usr );
                this.receiverFuture=createReceiverFuture();
              //  receiver.start();
            } else {
                throw new AuthException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return usr;
    }

    @Override
    public void close() throws IOException {
        socket.close();
        this.receiverFuture.cancel(true);
    }

}