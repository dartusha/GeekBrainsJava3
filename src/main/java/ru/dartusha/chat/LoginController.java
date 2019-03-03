package ru.dartusha.chat;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController  implements MessageSender, Initializable  {

    @FXML
    public TextField tfLogin;

    @FXML
    public TextField pfPassword;

    @FXML
    public Button btLogin;

    @FXML
    public Text txtLoginResult;

    Stage pSt;

    public Network network;

    public void setPS(Stage primaryStage) {
        pSt = primaryStage;
    }

    @FXML public void onLoginClicked(ActionEvent event) {
        network = null;
        try {
            network = new Network(Const.LOCAL_HOST, Const.PORT, (MessageSender) DataProcess.getParentController());
            System.out.println(tfLogin.getText()+ String.valueOf(pfPassword.getText()));
            network.authorize(tfLogin.getText(), String.valueOf(pfPassword.getText()));
            txtLoginResult.setText("");
            Stage stage = (Stage) btLogin.getScene().getWindow();
            stage.close();
            DataProcess.setNetwork(network);
            DataProcess.setCuruser(tfLogin.getText());
            DataProcess.getParentController().setUser();
            DataProcess.setPassword(pfPassword.getText());
        } catch (IOException ex) {
            ex.printStackTrace();
            txtLoginResult.setText("Ошибка сети");
        }
        catch (AuthException ex) {
            txtLoginResult.setText("Ошибка авторизации");
        }
}

    @FXML public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        try {
            this.network = new Network(Const.LOCAL_HOST, Const.PORT, this);//(MessageSender) primaryStage);
        } catch (IOException ex) {
            ex.printStackTrace();
            txtLoginResult.setText("Ошибка сети");
        }
    }

    public boolean isAuthSuccessful() {
        return network != null;
    }

    @Override
    public void submitMessage(Message msg) {
        System.out.println("test submit");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
