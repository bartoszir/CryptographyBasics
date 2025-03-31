package org.example;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DESWithPadding {
    private DESAlgorithm des = new DESAlgorithm();
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

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

        String decryptedHex = removePadding(result.toString());

        // upewniamy sie ze dlugosc jest parzysta
        if (decryptedHex.length() % 2 != 0) {
            decryptedHex = "0" + decryptedHex;
        }

        return decryptedHex;
    }

    private boolean isHex(String input) {
        return input.matches("^[0-9A-Fa-f]+$");
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

    public String chooseInputMessageFormat(String input) {
        return (!isHex(input)) ? textToHex(input) : input;
    }

    public String chooseOutputMessageFormat(boolean showAsHex, String output) {
        if (showAsHex) {
            return isHex(output) ? output : textToHex(output);
        } else {
            return isHex(output) ? hexToText(output) : output;
        }
    }

    public String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            // konwertujemy 8 bitow na int (32 bity)
            int v = bytes[i] & 0xFF;
            // przesuwamy bity w prawo o 4 pozycje, dzielimy przez 16. co daje nam wartosc starszego polbajtu
            // i pobieramy odpowiadajacy znak dla starszego polbajtu
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            // nakladamy maske bitowa ktora wyciaga 4 mlodsze bity (0-15) i pobieramy znak hex odpowiadajacy
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public byte[] hexToBytes(String text)
    {
        if (text == null) {
            return null;
        } else if (text.length() < 2) {
            return null;
        }
        else {
            if (text.length() % 2 != 0) {
                text+='0';
            }
            int length = text.length() / 2;
            byte[] output = new byte[length];
            for (int i = 0; i < length; i++)
            {
                try {
                    output[i] = (byte) Integer.parseInt(text.substring(i * 2, i * 2 + 2), 16);
                } catch (Exception e) {
                }
            }
            return output;
        }
    }
}
