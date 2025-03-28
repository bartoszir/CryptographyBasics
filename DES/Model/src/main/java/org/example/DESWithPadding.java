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
        if (decryptedHex.length() < 2) return decryptedHex;

        // Ostatni bajt określa ilość paddingu
        int padLength = 2 * Integer.parseInt(
                decryptedHex.substring(decryptedHex.length() - 2),
                16
        );

        if (padLength <= 0 || padLength > decryptedHex.length()) {
            throw new IllegalArgumentException("Invalid padding");
        }

        return decryptedHex.substring(0, decryptedHex.length() - padLength);
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
