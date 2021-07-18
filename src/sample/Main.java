package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/LoginScene.fxml"));
        Parent root = loader.load();
        // tieto dva riadky som tu nechal, to je klasicke spustanie hlavnej stage, ktore vygeneruje intellij
        primaryStage.setScene(new Scene(root, 1250, 850));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}