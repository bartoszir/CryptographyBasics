package org.example;

import java.math.BigInteger;
import java.util.Scanner;

public class UserInput {
    public void input() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose format of your message: ");
        System.out.println("[1]. Text (e.g. 'Hello World!')");
        System.out.println("[2]. Hexagonal (e.g. '48656C6C6F2C20576F726C6421')");
        System.out.print("(choice): ");
        int format = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter your message for encryption: ");
        String message = scanner.nextLine();
        System.out.println(message);

        // converting message to bytes
        BigInteger messageBigInt;
        if (format == 1) {
            byte[] messageBytes = message.getBytes();
            messageBigInt = new BigInteger(1, messageBytes); // 1 means that the number is positive
        } else if (format == 2) {
            byte[] messageBytes = hexStringToByteArray(message);
            messageBigInt = new BigInteger(message, 16);
        } else {
            System.out.println("Invalid format");
            return;
        }

        
        // testing converting results
//        System.out.println("---------------------- RESULT ----------------------");
//        System.out.print("Message in bytes: ");
//        for (byte b : messageBytes) {
//            System.out.print(String.format("%02X ", b));
//        }
//        System.out.println("\n----------------------------------------------------");


        // getting key
        System.out.println("Type in 64-bit key (in hex, e.g. '133457799BBCDFF1'): ");
        System.out.print("(key): ");
        String keyInput = scanner.nextLine();
        BigInteger key = new BigInteger(keyInput, 16);

        // choose encryption or decryption
        System.out.println("\nChoose one:");
        System.out.println(" [1]. encryption");
        System.out.println(" [2]. decryption");
        System.out.print("(choice): ");
        int option = scanner.nextInt();

        if (option == 1) {
//            BigInteger encryptedBytes = encryptBlock(messageBigInt, key);
//            System.out.println("Encrypted message (hex): " + encryptedBytes.toString(16));
        } else if (option == 2) {
//            BigInteger decryptedBytes = decryptBlock(messageBigInt, key);
//            System.out.println("Decrypted message: " + new String(decryptedBytes.toByteArray()));
        } else {
            System.out.println("Invalid option");
        }
    }

    public byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
