package ru.dartusha.chat;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller implements Initializable, MessageSender {
    @FXML
    public TextField tfMessage;

    @FXML
    public Text txtWelcome;

    @FXML
    public ListView lvMessages;

    @FXML
    public ListView lvUsers;

    @FXML
    public Button btSendMessage;

    @FXML
    public Button btChangeUsername;

    @FXML
    public TextField tfUsername;


    Stage primaryStage;

    private ObservableList<String> messageList;
    private ObservableList<String> userList;

    //private Network network;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageList = FXCollections.observableArrayList();
        userList = FXCollections.observableArrayList();
        lvUsers.setItems(userList);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void onSendMessageClicked() {
        String text = tfMessage.getText();
        Message msg = null;

        msg = new Message("", DataProcess.getCurUser(), tfMessage.getText());
        submitMessage(msg);
        DataProcess.getNetwork().sendMessage(msg.getText());
        if (text != null && !text.isEmpty()) {
            tfMessage.clear();
            tfMessage.requestFocus();
        }
    }

    public void setOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            onSendMessageClicked();
        }
    }

    ;

    public void submitMessage(Message message) {
        if (message.getText().contains("$")==false) {
            String result=message.getText();
            if (result.contains("/w")) {
                result = result.replace("/w", "(private)");
                if (result.indexOf(" ")!=-1) {
                    result = result.substring(result.indexOf(" "), result.length());
                }
            }
            if (result.contains("/a")) {
                result = result.replace("/a", "");
            }

            try {
                DataProcess.getUserLog().add(result);
            } catch (IOException e) {
                e.printStackTrace();
            }

           if (message.getText().contains("User "+DataProcess.getCurUser()+" connected")){
               for (String cur:DataProcess.getUserLog().getResArray().values()){
                   messageList.add(cur);
               }
           }



            messageList.add(result);//.replace("/w", "Для "));
            lvMessages.setItems(messageList);
        }

        if (message.getText().contains("$USERS:")){
            userList.clear();
            String[] array = message.getText().substring(18,message.getText().length()).split("\\,");
            for (String cur:array) {
                if (!DataProcess.getCurUser().equals(cur))
                    userList.add(cur);
            }
            lvUsers.setItems(userList);
        }

        if (message.getText().contains("connected")){
            userList.clear();
        }

    }

    public void onLvUsersClick() {
        String str = "/w " + lvUsers.getSelectionModel().getSelectedItem().toString();
        tfMessage.clear();
        tfMessage.setText(str);
    }


    public void shutdown() {
        try {
            DataProcess.getNetwork().sendMessage(Const.CMD_CLOSED);
            System.out.println("Client "+DataProcess.getCurUser()+" disconnected");
            DataProcess.getNetwork().close();
            DataProcess.getUserLog().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Platform.exit();
    }

    public void setUser(){
       // txtWelcome.setText("Добро пожаловать в чат, "+);
        tfUsername.setText(DataProcess.getCurUser());
    }

    public void onChangeUsernameClick() throws SQLException {
        String newUsername=tfUsername.getText();
        String curUser=DataProcess.getCurUser();

        if (dbWork.checkUserExists(newUsername)){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Пользователь с таким ником уже существует! Выберите другой.");
                alert.showAndWait();

                tfUsername.setText(curUser);
        }
        if (!(curUser.equals(tfUsername.getText()))){
            dbWork.changeUserName(curUser,newUsername);
            Message msg = null;

            DataProcess.getNetwork().sendMessage(Const.CMD_CHANGE_NAME+newUsername);
            DataProcess.setCurUser(newUsername);
            tfUsername.setText(newUsername);
            try {
                DataProcess.getUserLog().rename(newUsername);
            } catch (IOException e) {
                e.printStackTrace();
            }

            }

    }
}

