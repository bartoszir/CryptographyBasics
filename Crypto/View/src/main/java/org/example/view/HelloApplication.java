package org.example.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DESView.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);

        DESController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.setScene(scene);
        primaryStage.setTitle("DES");
        primaryStage.show();
    }
}
