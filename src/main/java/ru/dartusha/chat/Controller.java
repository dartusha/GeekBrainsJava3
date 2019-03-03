package ru.dartusha.chat;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
       // userList.addAll("ivan", "petr", "julia"); // пока фмксированный список
        lvUsers.setItems(userList);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void onSendMessageClicked() {
        String text = tfMessage.getText();
        Message msg = null;

        msg = new Message("", DataProcess.getCuruser(), tfMessage.getText());
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

            messageList.add(result);//.replace("/w", "Для "));
            lvMessages.setItems(messageList);
        }

        if (message.getText().contains("$USERS:")){
            String[] array = message.getText().substring(18,message.getText().length()).split("\\,");
            for (String cur:array) {
                if (!DataProcess.getCuruser().equals(cur))
                    userList.add(cur);
            }

            lvUsers.setItems(userList);
        }

        if (message.getText().contains("connected")){
            userList.clear();
            DataProcess.getNetwork().sendMessage("$GET_USERS");
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
            System.out.println("Client "+DataProcess.getCuruser()+" disconnected");
            DataProcess.getNetwork().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Platform.exit();
    }

    public void setUser(){

       // txtWelcome.setText("Добро пожаловать в чат, "+);
        tfUsername.setText(DataProcess.getCuruser());
    }

    public void onChangeUsernameClick() throws SQLException {
        String newUsername=tfUsername.getText();
        String curUser=DataProcess.getCuruser();

        try (Connection connection=DriverManager.getConnection("jdbc:sqlite:userDB.db")){
            //Statement statement=connection.createStatement();
           // ResultSet resultSet=statement.executeQuery("select 1 from users where username=?");
            PreparedStatement ps = connection.prepareStatement("select 1 from users where login=?");
            ps.setString(1, newUsername);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Пользователь с таким ником уже существует! Выберите другой.");
                alert.showAndWait();

                tfUsername.setText(curUser);
            }
            if (!(curUser.equals(tfUsername.getText()))){
                ps = connection.prepareStatement("update users set login=? where login=?");
                ps.setString(1, newUsername);
                ps.setString(2, curUser);
                ps.execute();
                Message msg = null;
                msg = new Message("", curUser, "Пользователь "+curUser+" изменил ник на "+newUsername);
                submitMessage(msg);

                DataProcess.getNetwork().sendMessage(Const.CMD_CHANGE_NAME+newUsername);

                DataProcess.getNetwork().sendMessage(Const.CMD_CLOSED);
                DataProcess.getNetwork().close();

                Network network = null;
                try {
                    network = new Network(Const.LOCAL_HOST, Const.PORT, (MessageSender) this);
                    network.authorize(newUsername,DataProcess.getPassword());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DataProcess.setNetwork(network);
                DataProcess.setCuruser(newUsername);
              //  DataProcess.getNetwork().sendMessage("Пользователь "+curUser+" изменил ник на "+newUsername);
             //   DataProcess.getNetwork().sendMessage("$GET_USERS");
                tfUsername.setText(newUsername);
            }
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

