package ru.dartusha.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
1. Написать консольный вариант клиент\серверного приложения, в котором пользователь может писать сообщения,
как на клиентской стороне, так и на серверной. Т.е. если на клиентской стороне написать «Привет», нажать Enter,
то сообщение должно передаться на сервер и там отпечататься в консоли. Если сделать то же самое на серверной стороне,
то сообщение передается клиенту и печатается у него в консоли. Есть одна особенность, которую нужно учитывать:
клиент или сервер может написать несколько сообщений подряд. Такую ситуацию необходимо корректно обработать.
 */

/*
Сделано:
1. отправка сообщения с клиента на сервер
2. отправка сообщений на клиенты с сервера
*/

public class EchoServer {


    public static void main(String[] args) {

        List <Socket> sockets = Collections.synchronizedList(new ArrayList<Socket>());
        //массив потоков, чтобы на все клиенты передалось сообщение от сервера

        try (ServerSocket serverSocket = new ServerSocket(Const.PORT)) {
            System.out.println("Server started!");
            BufferedReader inputCon = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                Socket socket = serverSocket.accept();
                sockets.add(socket);
                System.out.println("Client connected!");

                Thread io=
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                while (true) {
                                    String inputStr = null;
                                    try {
                                        inputStr = "Сообщение от сервера: "+inputCon.readLine();
                                        for (Socket i:sockets) {
                                            DataOutputStream out = null;
                                            try {
                                                out = new DataOutputStream(i.getOutputStream());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            out.writeUTF(inputStr);
                                            out.flush();

                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println(inputStr);
                                }
                            }
                        });
                io.start();


                Thread oi=
                        new Thread(new Runnable() {

                            @Override
                            public void run() {

                                try (DataInputStream inp = new DataInputStream(socket.getInputStream());
                                     DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
                                    boolean flag=true;
                                    while (flag) {
                                        String msg = inp.readUTF();
                                        if (msg.equals(Const.CMD_CLOSED)){
                                            flag=false;
                                            System.out.println("Client disconnected");
                                            inp.close();
                                            out.close();
                                            sockets.remove(socket);
                                            socket.close();
                                        }
                                        else {
                                            System.out.println("Сообщение от клиента: " + msg);
                                            out.writeUTF(msg);
                                            out.flush();
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                oi.start();

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}