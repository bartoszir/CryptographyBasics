package org.example;

import java.math.BigInteger;
import java.util.Collections;

public class DESWithPadding {
    private DESAlgorithm des = new DESAlgorithm();

    private String addPadding(String hexMessage) {
        int padLength = 16 - (hexMessage.length() % 16);
        if (padLength == 16) {
            padLength = 0;
        }

        // Każdy bajt paddingu równy jego długości
        String padding = String.format("%02X", padLength/2).repeat(padLength/2);
        return hexMessage + padding;
    }

    private String removePadding(String decryptedHex) {
        // Minimalna długość to 2 znaki (1 bajt) i musi być parzysta
        if (decryptedHex.length() < 2 || decryptedHex.length() % 2 != 0) {
            return decryptedHex;
        }

        // Ostatni bajt to potencjalna długość paddingu
        String lastByteStr = decryptedHex.substring(decryptedHex.length() - 2);
        int padByteValue;

        try {
            padByteValue = Integer.parseInt(lastByteStr, 16);
        } catch (NumberFormatException e) {
            return decryptedHex; // Nie można parsować - prawdopodobnie nie ma paddingu
        }

        // Prawidłowa wartość paddingu to 1-8
        if (padByteValue < 1 || padByteValue > 8) {
            return decryptedHex; // Nieprawidłowa wartość - brak paddingu
        }

        // Sprawdź czy cały padding jest poprawny
        int padStart = decryptedHex.length() - 2 * padByteValue;
        if (padStart < 0) {
            return decryptedHex; // Za mało danych dla deklarowanego paddingu
        }

        // Sprawdź czy wszystkie bajty paddingu mają tę samą wartość
        String padding = decryptedHex.substring(padStart);
        String expectedPadding = String.format("%02X", padByteValue).repeat(padByteValue);
        if (!padding.equals(expectedPadding)) {
            return decryptedHex; // Padding nie jest jednolity
        }

        return decryptedHex.substring(0, padStart);
    }

    public String encrypt(String hexMessage, String hexKey) {
        String paddedMessage = addPadding(hexMessage);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < paddedMessage.length(); i += 16) {
            String block = paddedMessage.substring(i, Math.min(i + 16, paddedMessage.length()));
            BigInteger encrypted = des.encode(new BigInteger(block, 16), new BigInteger(hexKey, 16));
            result.append(String.format("%016X", encrypted));
        }
        return result.toString();
    }

    public String decrypt(String hexMessage, String hexKey) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < hexMessage.length(); i += 16) {
            String block = hexMessage.substring(i, Math.min(i + 16, hexMessage.length()));
            BigInteger decrypted = des.decode(new BigInteger(block, 16), new BigInteger(hexKey, 16));
            result.append(String.format("%016X", decrypted));
        }
        return removePadding(result.toString());
    }
}
