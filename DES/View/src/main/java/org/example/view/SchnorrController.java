package org.example.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class SchnorrController {
    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }

    @FXML
    private void handleDesViewClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DESView.fxml"));
            Parent desRoot = loader.load();

            DESController controller = loader.getController();
            controller.setPrimaryStage(primaryStage); // przekazujemy stage

            Scene scene = new Scene(desRoot, 900, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("DES");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSchnorrViewClicked(ActionEvent event) {

    }
}
