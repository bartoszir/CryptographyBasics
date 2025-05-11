package org.example;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Utils {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

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
