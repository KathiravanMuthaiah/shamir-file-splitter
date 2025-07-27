package com.mikcore.shamir;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.Base64;

public class FileSplitter {

    public static void processInputFile(String inputPath, int k, int n) throws Exception {
        Path inputFile = Paths.get(inputPath);
        String filename = inputFile.getFileName().toString();
        byte[] fileBytes = Files.readAllBytes(inputFile);

        // Encode to base64
        String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);
        String base64File = "processed/" + filename + "_base64.txt";
        Files.createDirectories(Paths.get("processed"));
        Files.writeString(Paths.get(base64File), base64Encoded);
        System.out.println("Base64 saved: " + base64File);

        // Determine chunk size in bytes (based on key strength)
        int chunkSizeBytes = (int) Math.ceil(k / 8.0);  // E.g., 3 → 256 → 32 bytes
        int totalChunks = (int) Math.ceil((double) base64Encoded.length() / chunkSizeBytes);

        // Split and save chunks
        for (int i = 0; i < totalChunks; i++) {
            int start = i * chunkSizeBytes;
            int end = Math.min(start + chunkSizeBytes, base64Encoded.length());
            String chunk = base64Encoded.substring(start, end);
            String chunkFile = "processed/" + filename + "_g" + (i + 1) + ".txt";
            Files.writeString(Paths.get(chunkFile), chunk);
        }

        // Run Shamir SSS on each chunk
        ShamirSplitter.splitChunks(filename, totalChunks, k, n);
    }
}

