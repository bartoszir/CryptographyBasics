package org.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDESDao {
    public byte[] loadBinaryFile(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            int space = fis.available();
            byte[] input = new byte[space];
            fis.read(input);
            return input;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveBinaryFile(byte[] data, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
