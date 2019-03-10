package ru.dartusha.chat;

public class DataProcess {
    public static Network network;
    public static String curUser;
    public static Controller parentController;
    public static String password;
    public static UserLog userLog;

    public DataProcess(Network network) {

    }

    public static void setNetwork(Network netw) {
        network = netw;
    }

    public static void setCurUser(String cur) {
        curUser = cur;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        DataProcess.password = password;
    }

    public static String getCurUser() {
        return curUser;
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

    public static void setUserLog(UserLog userLog) {
        DataProcess.userLog = userLog;
    }

    public static UserLog getUserLog(){
        return DataProcess.userLog;
    }
}
