package org.example;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class UserInput {
    DESWithPadding des;
    Scanner scanner;

    public UserInput() {
        this.des = new DESWithPadding();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n------------------------ UI ------------------------");
            System.out.println("Choose operation:");
            System.out.println("[1]. Encrypt message");
            System.out.println("[2]. Decrypt message");
            System.out.println("[3]. Exit");
            System.out.print("(choice): ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 3) {break;}

            switch (choice) {
                case 1 -> handleEncryption();
                case 2 -> handleDecryption();
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void handleEncryption() {
        System.out.println("\nChoose input format:");
        System.out.println("[1]. Text (e.g. 'Hello World')");
        System.out.println("[2]. Hexadecimal (e.g. '48656C6C6F')");
        System.out.print("(choice): ");
        int format = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter message: ");
        String message = scanner.nextLine();

        System.out.print("Enter 64-bit key (16 chars): ");
        String key = scanner.nextLine();
        try {
            String hexMessage = (format == 1) ? textToHex(message) : message;
            String encrypted = des.encrypt(hexMessage, key);
            System.out.println("\nEcnrypted message (hex): " + encrypted);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void handleDecryption() {
        System.out.print("Enter encrypted message (hex): ");
        String message = scanner.nextLine();

        System.out.print("Enter 64-bit key (16 chars): ");
        String key = scanner.nextLine();

        System.out.println("\nChoose output format:");
        System.out.println("[1]. Text");
        System.out.println("[2]. Hexadecimal");
        System.out.print("(choice): ");
        int format = scanner.nextInt();
        scanner.nextLine();

        try {
            String decryptedHex = des.decrypt(message, key);
            String result = (format == 1) ? hexToText(decryptedHex) : decryptedHex;
            System.out.println("\nDecrypted message: " + result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public boolean testKey(String keyInput) {
        BigInteger key = new BigInteger(keyInput, 16);
        // klucz musi miec dokladnie 64 bity
        if (key.bitLength() != 64) {
            return false;
        }

//        // sprawdzamy bity parzystosci dla kazdego bajtu
//        for (int i = 0; i < 8; i++) {
//            // wyodrebniamy i-ty bajt (liczac od lewej)
//            int byteValue = key.shiftRight(64 - 8 * (i + 1)).and(new BigInteger("FF", 16)).intValue();
//
//            // liczymy ilosc jedynek w 7 bitach danych
//            int bitCount = Integer.bitCount(byteValue >>> 1);
//
//            if ((bitCount + (byteValue & 1)) % 2 == 0) {
//                return false;
//            }
//        }
        return true;
    }


    private String textToHex(String text) {
        return String.format("%0" + (text.length() * 2) + "X", new BigInteger(1, text.getBytes(StandardCharsets.UTF_8)));
    }


    private String hexToText(String hex) {
        byte[] bytes = new BigInteger(hex, 16).toByteArray();

        if (bytes.length > 0 && bytes[0] == 0) {
            bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }
}
