package org.example.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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
    @FXML private ToggleButton txtDecryptedTextButton;
    @FXML private ToggleButton hexDecryptedTextButton;

    private final ToggleGroup formatToggleGroup = new ToggleGroup();

    @FXML
    public void initialize() {
        txtDecryptedTextButton.setToggleGroup(formatToggleGroup);
        hexDecryptedTextButton.setToggleGroup(formatToggleGroup);

        hexDecryptedTextButton.setSelected(true);

         updateButtonStyles();

        formatToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            updateButtonStyles();
        });
    }

    @FXML
    private void encryptButtonClicked(ActionEvent event) {
        try {
            String plainTextInput = encryptionText.getText();
            String keyInput = encryptionKey.getText();

            if (!keyInput.matches("[0-9A-Fa-f]{16}")) {
                cipherTextArea.setText("Key must be 8 characters (64 bits) long!");
                return;
            }

            String plainText = des.chooseInputMessageFormat(plainTextInput);

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

    private void updateButtonStyles() {
        if (txtDecryptedTextButton.isSelected()) {
            txtDecryptedTextButton.setStyle("-fx-background-color: #000000; -fx-text-fill: WHITE;");
            hexDecryptedTextButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #6f6f6f; -fx-border-color: #6f6f6f; -fx-border-width: 1px; -fx-border-radius: 3px;");
        } else {
            hexDecryptedTextButton.setStyle("-fx-background-color: #000000; -fx-text-fill: WHITE;");
            txtDecryptedTextButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #6f6f6f; -fx-border-color: #6f6f6f; -fx-border-width: 1px; -fx-border-radius: 3px;");
        }
    }

    @FXML
    private void txtDecryptedTextButtonToggled(ActionEvent event) {
        hexDecryptedTextButton.setSelected(false);
        updateButtonStyles();
        refreshOutputFormat();
    }

    @FXML
    private void hexDecryptedTextButtonToggled(ActionEvent event) {
        txtDecryptedTextButton.setSelected(false);
        updateButtonStyles();
        refreshOutputFormat();
    }

    private void refreshOutputFormat() {
        String outputMessage = decryptedTextArea.getText();
        if (!outputMessage.isEmpty()) {
            boolean showAsHex = hexDecryptedTextButton.isSelected();
            String formattedText = des.chooseOutputMessageFormat(showAsHex, outputMessage);
            decryptedTextArea.clear();
            decryptedTextArea.setText(formattedText);
        }
    }

    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }


}
