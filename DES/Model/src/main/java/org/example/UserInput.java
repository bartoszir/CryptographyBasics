package org.example;

import java.math.BigInteger;
import java.util.Scanner;

public class UserInput {
    DESAlgorithm des;

    public UserInput() {
        this.des = new DESAlgorithm();
    }


    public void input() {
        Scanner scanner = new Scanner(System.in);
        int endOption = 2;
        while (endOption != 1) {
            System.out.println("Choose one:");
            System.out.println(" [1]. Encryption");
            System.out.println(" [2]. Decryption");
            System.out.print("(choice): ");
            int option = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Enter message:");
            System.out.print("(input): ");
            String messageInput = scanner.nextLine();
            BigInteger message = new BigInteger(messageInput, 16);

            System.out.println("Enter 64-bit key (16 hex chars):");
            System.out.print("(input): ");
            String keyInput = scanner.nextLine();
            BigInteger key = new BigInteger(keyInput, 16);

            if (option == 1) {
                BigInteger encryptedMsg = des.encode(message, key);
                System.out.println("\nEncrypted message (hex): " + encryptedMsg.toString(16).toUpperCase());
            } else if (option == 2) {
                BigInteger decryptedMsg = des.decode(message, key);
                System.out.println("\nDecrypted message (hex): " + decryptedMsg.toString(16).toUpperCase());
            } else {
                System.out.println("Invalid option");
            }

            System.out.println("(.) Co dalej chcesz zrobić?");
            System.out.println(" [1]. Wyjść z programu");
            System.out.println(" [2]. Wprowadzić nowe dane");
            System.out.print("(choice): ");
            endOption = scanner.nextInt();
            scanner.nextLine();
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
