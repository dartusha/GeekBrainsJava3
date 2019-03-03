package ru.dartusha.chat;

import javafx.stage.Stage;

public class DataProcess {
    public static Network network;
    public static String curuser;
    public static Controller parentController;
    public static String password;

    public DataProcess(Network network) {

    }

    public static void setNetwork(Network netw) {
        network = netw;
    }

    public static void setCuruser(String cur) {
        curuser = cur;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        DataProcess.password = password;
    }

    public static String getCuruser() {
        return curuser;
    }

    public  static Network getNetwork(){
        return network;
    }

    public static void setParentController(Controller parentContr){
        parentController=parentContr;
    }

    public static Controller getParentController(){
        return parentController;
    }
}
