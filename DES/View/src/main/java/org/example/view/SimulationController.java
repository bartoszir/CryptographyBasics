package org.example.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import org.example.DESWithPadding;
public class SimulationController {
    private Stage primaryStage;
    private final DESWithPadding des = new DESWithPadding();

    @FXML private TextField encryptionText;
    @FXML private TextField encryptionKey;
    @FXML private TextArea cipherTextArea;

    @FXML private TextField decryptionText;
    @FXML private TextField decryptionKey;
    @FXML private TextArea decryptedTextArea;

    @FXML
    private void encryptButtonClicked(ActionEvent event) {
        try {
            String plainText = encryptionText.getText();
            String keyInput = encryptionKey.getText();

            if (!keyInput.matches("[0-9A-Fa-f]{16}")) {
                cipherTextArea.setText("Key must be 8 characters (64 bits) long!");
                return;
            }

            String encrypted = des.encrypt(plainText, keyInput);
            cipherTextArea.setText(encrypted);
            decryptionKey.setText(keyInput);
            decryptionText.setText(encrypted);

        } catch (Exception e) {
            cipherTextArea.setText("Error during encryption: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void generateMsgClicked(ActionEvent event) {
        try {
            encryptionText.clear();
            encryptionText.setText("0123456789ABCDEF");
        } catch (Exception e) {
            cipherTextArea.setText("Error during generating: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void generateKeyClicked(ActionEvent event) {
        try {
            encryptionKey.clear();
            encryptionKey.setText("0123456789ABCDEF");
        } catch (Exception e) {
            cipherTextArea.setText("Error during generating: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void decryptButtonClicked(ActionEvent event) {
        try {
            String encryptedText = decryptionText.getText();
            String keyInput = decryptionKey.getText();

            if (!keyInput.matches("[0-9A-Fa-f]{16}")) {
                decryptedTextArea.setText("Key must be 8 characters (64 bits) long!");
                return;
            }

            String decrypted = des.decrypt(encryptedText, keyInput);
            decryptedTextArea.setText(decrypted);
        } catch (Exception e) {
            decryptedTextArea.setText("Error during decryption: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }


}
