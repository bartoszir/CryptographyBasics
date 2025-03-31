package org.example.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import org.example.DESWithPadding;
import org.example.FileDESDao;

import java.io.File;

import java.util.Base64;


public class SimulationController {
    private Stage primaryStage;
    private final DESWithPadding des = new DESWithPadding();
    private final FileDESDao fileDao = new FileDESDao();
    private String encryptionTextData;
    private String cipherTextData;
    private String decryptionTextData;
    private boolean encryptingFile = false;
    private boolean decryptingFile = false;

    @FXML private TextField encryptionText;
    @FXML private TextField encryptionKey;
    @FXML private TextArea cipherTextArea;

    @FXML private TextField decryptionText;
    @FXML private TextField decryptionKey;
    @FXML private TextArea decryptedTextArea;
    @FXML private ToggleButton txtDecryptedTextButton;
    @FXML private ToggleButton hexDecryptedTextButton;

    @FXML private ToggleButton encryptingFileButton;
    @FXML private ToggleButton decryptingFileButton;

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

            encryptionTextData = des.encrypt(plainText, keyInput);

            if (!encryptingFile) {
                byte[] encryptedBytes = des.hexToBytes(encryptionTextData);
//                String rawString = new String(encryptedBytes, StandardCharsets.UTF_8);
//                cipherTextArea.setText(rawString);
                String base64String = Base64.getEncoder().encodeToString(encryptedBytes);
                cipherTextArea.setText(base64String);
                decryptionText.setText(base64String);



//                cipherTextArea.setText(encryptionTextData);
//                decryptionText.setText(encryptionTextData);

            } else {
                showInfoDialog("Encoding", "Successfully encrypted the file. Now you can save it");
                encryptingFileButtonToggled();
                handleSaveCipherText();
                encryptionText.clear();
            }
            decryptionKey.setText(keyInput);


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
            String cipherTextInput = decryptionText.getText();
            String keyInput = decryptionKey.getText();
            String encryptedText;

            if (!decryptingFile) {
                if (cipherTextInput.matches("[0-9A-Fa-f]")) {
                    encryptedText = cipherTextInput;
                } else {
//                    encryptedText = des.bytesToHex(cipherTextInput.getBytes());
                    byte[] bytes = Base64.getDecoder().decode(cipherTextInput);
                    encryptedText =  des.bytesToHex(bytes);
                }
            } else {
                encryptedText = cipherTextInput;
            }


            if (!keyInput.matches("[0-9A-Fa-f]{16}")) {
                decryptedTextArea.setText("Key must be 8 characters (64 bits) long!");
                return;
            }

            decryptionTextData = des.decrypt(encryptedText, keyInput);

            if (!decryptingFile) {
                decryptedTextArea.setText(decryptionTextData);
                updateButtonStyles();
                refreshOutputFormat();
            } else {
                showInfoDialog("Decryption", "Successfully decrypted the file. Now you can save it");
                decryptingFileButtonToggled();
                handleSaveDecryptedText();
                decryptionText.clear();
            }
        } catch (Exception e) {
            decryptedTextArea.setText("Error during decryption: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateButtonStyles() {
        if (txtDecryptedTextButton.isSelected()) {
            txtDecryptedTextButton.setStyle("-fx-background-color: #000000; -fx-text-fill: WHITE;");
            hexDecryptedTextButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #6f6f6f; " +
                    "-fx-border-color: #6f6f6f; -fx-border-width: 1px; -fx-border-radius: 3px;");
        } else {
            hexDecryptedTextButton.setStyle("-fx-background-color: #000000; -fx-text-fill: WHITE;");
            txtDecryptedTextButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #6f6f6f; " +
                    "-fx-border-color: #6f6f6f; -fx-border-width: 1px; -fx-border-radius: 3px;");
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

    @FXML
    private void handleLoadKey() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Key File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DES File", "*.DESKey"));
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            try  {
//                String key = fileDao.loadFromFile(file.getAbsolutePath());
                byte[] input = fileDao.loadBinaryFile(file.getAbsolutePath());
                String hexKey = des.bytesToHex(input);
                encryptionKey.clear();
                encryptionKey.setText(hexKey);
                decryptionKey.clear();
                decryptionKey.setText(hexKey);
            } catch (Exception e) {
                showErrorDialog("Load Error", "Failed to load key from file: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSaveKey() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Key File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DES File", "*.DESKey"));

        File file = fileChooser.showSaveDialog(primaryStage);
        String key = encryptionKey.getText();
        if (file != null) {
            try {
                byte[] input = des.hexToBytes(key);
                fileDao.saveBinaryFile(input, file.getAbsolutePath());
            } catch (Exception e) {
                showErrorDialog("Save Error", "Failed to save key to the file: " + e.getMessage());
            }
        }
        showInfoDialog("Save", "Key saved successfully to file: " + file.getAbsolutePath());
    }

    @FXML
    private void handleLoadEncryptionText(){
        encryptingFileButtonToggled();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File For Encryption");
        fileChooser.getExtensionFilters().addAll();

        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            try {
                byte[] input = fileDao.loadBinaryFile(file.getAbsolutePath());
//                String text = fileDao.loadFromFile(file.getAbsolutePath());
                String hexData = des.bytesToHex(input);
                encryptionText.clear();
                encryptionText.setText(hexData);
            } catch (Exception e) {
                System.out.println("Error during loading from file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSaveCipherText(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File Encrypted File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DES File", "*.DESCipher"));

        File file = fileChooser.showSaveDialog(primaryStage);
        String text = encryptionTextData;

        if (file != null) {
            try {
                byte[] output = des.hexToBytes(text);
//                fileDao.saveToFile(text, file.getAbsolutePath());
                fileDao.saveBinaryFile(output, file.getAbsolutePath());
            } catch (Exception e) {
                showErrorDialog("Save Error", "Failed to save cipher text to file: " + e.getMessage());
            }
        }
        showInfoDialog("Save", "Cipher text saved successfully to file: " + file.getAbsolutePath());
    }

    @FXML
    private void handleLoadCipherText(){
        decryptingFileButtonToggled();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File For Decryption");
        fileChooser.getExtensionFilters().addAll();

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
//                String text = fileDao.loadFromFile(file.getAbsolutePath());
                byte[] input = fileDao.loadBinaryFile(file.getAbsolutePath());
                String hexData = des.bytesToHex(input);
                decryptionText.clear();
                decryptionText.setText(hexData);
            } catch (Exception e) {
                showErrorDialog("Load Error", "Failed to load from file: " + e.getMessage());
            }
        }
        showInfoDialog("Read", "Successfully read encrypted file: " + file.getAbsolutePath());
    }

    @FXML
    private void handleSaveDecryptedText(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File For Decryption");
        fileChooser.getExtensionFilters().addAll();

        File file = fileChooser.showSaveDialog(primaryStage);
        String text = decryptionTextData;

        if (file != null) {
            try {
                byte[] output = des.hexToBytes(text);
                fileDao.saveBinaryFile(output, file.getAbsolutePath());
            } catch (Exception e) {
                System.out.println("Error during saving to file: " + e.getMessage());
                showErrorDialog("Save Error", "Failed to save to file: " + e.getMessage());
            }
        }
        showInfoDialog("Save", "Decrypted text saved successfully to file: " + file.getAbsolutePath());
    }

    @FXML
    private void encryptingFileButtonToggled() {
        encryptingFileButton.setSelected(!encryptingFile);
        encryptingFile = encryptingFileButton.isSelected();
    }

    @FXML
    private void decryptingFileButtonToggled() {
        decryptingFileButton.setSelected(!decryptingFile);
        decryptingFile = decryptingFileButton.isSelected();
    }

    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }

    private static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
