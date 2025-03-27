package org.example;
import java.math.BigInteger;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        UserInput userInput = new UserInput();
////        userInput.input();
//
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Message (hex): ");
//        String message = scanner.nextLine();
//        System.out.print("Key (hex): ");
//        String key = scanner.nextLine();
//
//        System.out.println();
//        byte[] messageBytes = message.getBytes();
//        for (byte b : messageBytes) {
//            System.out.print(String.format("%02X ", b));
//        }
//        System.out.println();
//
//        System.out.println("Bitowa reprezentacja (grupy 4-bitowe):");
//        for (int i = 0; i < message.length(); i++) {
//            char c = message.charAt(i);
//            int value = Character.digit(c, 16);
//            String bits = String.format("%4s", Integer.toBinaryString(value).replace(' ', '0'));
//
//            System.out.print(bits + " ");
//        }

        DESAlgorithm des = new DESAlgorithm();
        BigInteger cipherText = des.encode();
//        byte[] bytes = cipherText.toByteArray();
        System.out.println("\n------------------------ RESULTS ------------------------");
        System.out.println("DEC: " + cipherText);
        System.out.println("HEX: " + cipherText.toString(16).toUpperCase());
        System.out.print("BIN: ");
        System.out.println(String.format("%64s", cipherText.toString(2)).replace(' ', '0'));

        System.out.println();
        BigInteger tmp = new BigInteger("0123456789ABCDEF", 16);
        String binaryTmp = String.format("%64s", tmp.toString(2)).replace(' ', '0');
        System.out.println(binaryTmp);

    }
}