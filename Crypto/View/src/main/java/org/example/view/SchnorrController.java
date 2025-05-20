package org.example.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.Schnorr;
import org.example.Utils;

import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class SchnorrController {
    private Stage primaryStage;

    @FXML private TextArea plainTextArea;
    @FXML private TextArea signatureTextArea;
    @FXML private RadioButton fileRadioButton;
    @FXML private RadioButton textAreaRadioButton;
    private ToggleGroup inputToggleGroup = new ToggleGroup();

    Schnorr schnorr = new Schnorr();
    Utils utils = new Utils();

//    private byte[] plainText;
//    private byte[] signature;
//    private boolean fromFile = false;
    private byte[] binaryInput = null; // z pliku
    private byte[] activeMessage = null; // to, co zostanie podpisane
    private byte[] signature = null;


    @FXML
    public void initialize() {
        fileRadioButton.setToggleGroup(inputToggleGroup);
        textAreaRadioButton.setToggleGroup(inputToggleGroup);
        textAreaRadioButton.setSelected(true); // domyślnie tryb tekstowy

        inputToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            updateActiveMessage();
        });
        updateActiveMessage(); // ustawienie początkowe
    }

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
    private void handleSchnorrViewClicked(ActionEvent event) {}

    @FXML
    private void loadPlainTextFromFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            try {
                binaryInput = utils.loadBinaryFile(file.getAbsolutePath());
                plainTextArea.clear();
                plainTextArea.setText(new String(binaryInput));
//                plainTextArea.setText("[Plik binarny załadowany: " + file.getName() + "]");
                fileRadioButton.setSelected(true);
                updateActiveMessage();
            } catch (Exception e) {
                showErrorDialog("Error while loading from file", e.getMessage());
            }
        }
    }

    @FXML
    private void loadSignatureFromFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Signature");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Signature File", "*.sig"));

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
//            try {
//                signature = utils.loadBinaryFile(file.getAbsolutePath());
//                String signatureText = new String(signature);
//                signatureTextArea.setText(signatureText);
//                showInfoDialog("Load", "Signature loaded successfully.");
//            } catch (Exception e) {
//                showErrorDialog("Load Error", "Failed to load signature: " + e.getMessage());
//                e.printStackTrace();
//            }
            try {
                String signatureText = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
                String[] lines = signatureText.split("\n");
                if (lines.length != 2) {
                    showErrorDialog("Load Error", "Invalid signature file format (must have 2 lines: e and y).\nLoaded lines: " + lines.length);
                    return;
                }
                String formatted = lines[0].trim() + "\n" + lines[1].trim();
                signatureTextArea.setText(formatted);
                showInfoDialog("Load", "Signature loaded successfully.");
            } catch (Exception e) {
                showErrorDialog("Load Error", "Failed to load signature: " + e.getMessage());
            }
        }
    }

    @FXML
    private void signButtonClicked(ActionEvent event) {
        try {
//            String message = plainTextArea.getText();
            updateActiveMessage();
            if (activeMessage == null || activeMessage.length == 0) {
                showErrorDialog("Signing Error", "Plain text cannot be empty");
                return;
            }

            System.out.println("SIGNING - SHA256 of message: " +
                    utils.toHex(java.security.MessageDigest.getInstance("SHA-256").digest(activeMessage)));
            BigInteger[] signature = schnorr.sign(activeMessage);

            BigInteger e = signature[0];
            BigInteger y = signature[1];
            System.out.println("SIGNED E = " + e.toString(16));
            System.out.println("SIGNED Y = " + y.toString(16));

            //String formatted = "e:\n" + utils.toHex(e) + "\n\ny:\n" + utils.toHex(y);
            String formatted = utils.toHex(e) + "\n" + utils.toHex(y);
            signatureTextArea.setText(formatted);
        } catch (Exception e) {
            showErrorDialog("Signing Error", e.getMessage());
        }
    }

    @FXML
    private void verifyButtonClicked(ActionEvent event) {
        try {
            updateActiveMessage();
            if (activeMessage == null || activeMessage.length == 0) {
                showErrorDialog("Verification Error", "No message to verify.");
                return;
            }

//            String message = plainTextArea.getText();
//            String signatureText = signatureTextArea.getText().trim();

            String[] lines = signatureTextArea.getText().trim().split("\n");
            if (lines.length != 2) {
                showErrorDialog("Verification Error", "Invalid signature format.");
                return;
            }

            BigInteger e = new BigInteger(lines[0].trim(), 16);
            BigInteger y = new BigInteger(lines[1].trim(), 16);
            System.out.println("E parsed = " + e.toString(16));
            System.out.println("Y parsed = " + y.toString(16));

            System.out.println("VERIFYING - SHA256 of message: " +
                    utils.toHex(java.security.MessageDigest.getInstance("SHA-256").digest(activeMessage)));
            boolean result = schnorr.verify(activeMessage, e, y);
            if (result) {
                showInfoDialog("Verification Success", "Signature verified successfully.");
            } else {
                showErrorDialog("Verification Failed", "Signature is invalid.");
            }
        } catch (Exception e) {
            showErrorDialog("Verification Error", e.getMessage());
        }
    }

    @FXML
    private void savePlainTextButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Message");
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            try {
//                String messageText = plainTextArea.getText();
//                byte[] messageBytes = messageText.getBytes();
                byte[] messageBytes = textAreaRadioButton.isSelected()
                        ? plainTextArea.getText().getBytes(StandardCharsets.UTF_8)
                        : binaryInput;
                utils.saveBinaryFile(messageBytes, file.getAbsolutePath());
                showInfoDialog("Save", "Message saved successfully.");
            } catch (Exception e) {
                showErrorDialog("Save Error", "Failed to save message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void saveSignatureButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Signature");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Signature File", "*.sig"));

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
//                String signatureText = signatureTextArea.getText();
//                byte[] signature = signatureText.trim().getBytes();
//                byte[] signature = signatureTextArea.getText().trim().getBytes(StandardCharsets.UTF_8);
//                utils.saveBinaryFile(signature, file.getAbsolutePath());
//                showInfoDialog("Save", "Signature saved successfully.");
                String signatureText = signatureTextArea.getText().trim();
                Files.write(Paths.get(file.getAbsolutePath()), signatureText.getBytes(StandardCharsets.UTF_8));
                showInfoDialog("Save", "Signature saved successfully.");
            } catch (Exception e) {
                showErrorDialog("Save Error", "Failed to save signature: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void fileRadioButtonToggled(ActionEvent event) {
        updateActiveMessage();
    }

    @FXML
    private void textAreaRadioButtonToggled(ActionEvent event) {
        updateActiveMessage();
    }

    private void updateActiveMessage() {
        activeMessage = fileRadioButton.isSelected()
                ? binaryInput
                : plainTextArea.getText().getBytes(StandardCharsets.UTF_8);
    }

    @FXML
    private void loadPrivateKeyButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Private Key");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Private Key", "*.SchnorrSecKey"));

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                schnorr.loadPrivateKey(file.getAbsolutePath());
                showInfoDialog("Load", "Private key loaded successfully.");
            } catch (Exception e) {
                showErrorDialog("Load Error", "Failed to load private key: " + e.getMessage());
            }
        }
    }

    @FXML
    private void loadPublicKeyButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Public Key");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Public Key", "*.SchnorrPubKey"));

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                schnorr.loadPublicKey(file.getAbsolutePath());
                showInfoDialog("Load", "Public key loaded successfully.");
            } catch (Exception e) {
                showErrorDialog("Load Error", "Failed to load public key: " + e.getMessage());
            }
        }
    }

    @FXML
    private void savePublicKeyButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Public Key");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Public Key", "*.SchnorrPubKey"));

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                schnorr.savePublicKey(file.getAbsolutePath());
                showInfoDialog("Save", "Public key saved successfully.");
            } catch (Exception e) {
                showErrorDialog("Save Error", "Failed to save public key: " + e.getMessage());
            }
        }
    }

    @FXML
    private void savePrivateKeyButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Private Key");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Private Key", "*.SchnorrSecKey"));

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                schnorr.savePrivateKey(file.getAbsolutePath());
                showInfoDialog("Save", "Private key saved successfully.");
            } catch (Exception e) {
                showErrorDialog("Save Error", "Failed to save private key: " + e.getMessage());
            }
        }
    }

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
