package ru.dartusha.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadMaster {
    ExecutorService executorService = Executors.newCachedThreadPool();

    ThreadMaster(){

    }

    public void addClientThread(BufferedReader inputCon, ChatServer server) {
        executorService.execute(() -> {
            boolean flag=true;
            while (flag) {
                String inputStr = null;
                try {
                    inputStr = inputCon.readLine();
                    for (ClientHandler client : server.clientHandlerMap.values()) {
                        System.out.println("sending:"+client.getUsername());
                        client.sendMessage("server", inputStr, "");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    flag=false;
                }
                System.out.format("Сообщение от сервера: %s", inputStr);
                System.out.println("");
            }
        }
        );
    }

    public void startTimer(String userName, Socket socket,Map<User, ClientHandler> clientHandlerMap){
        executorService.execute(() -> {
            try {
                System.out.printf("You have %d seconds for login.", Const.TIMEOUT);
                System.out.println("");
                Thread.sleep(1000 * Const.TIMEOUT);
                if (clientHandlerMap.get(userName) == null) {
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
        );
    }


}