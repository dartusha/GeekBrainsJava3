package ru.dartusha.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
Урок 4. Задание 1.
Создать окно для клиентской части чата: большое текстовое поле для отображения переписки в центре окна.
        Однострочное текстовое поле для ввода сообщений и кнопка для отсылки сообщений на нижней панели.
        Сообщение должно отсылаться либо по нажатию кнопки на форме, либо по нажатию кнопки Enter.
        При «отсылке» сообщение перекидывается из нижнего поля в центральное.
        */

public class Chat extends Application {

    public Chat(){

    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/face.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        Scene scene = new Scene(root);
        DataProcess.setParentController(controller);
        primaryStage.setTitle(Const.CHAT);

        primaryStage.setScene(scene);
        primaryStage.setOnHidden(e -> controller.shutdown());

        primaryStage.show();

        LoginDialog login=new LoginDialog(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }

}
