package com.mikcore.shamir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class FileSplitter {

    public static void processInputFile(String inputPath, int keySize, int k, int n) throws Exception {
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
        // Step 1: Get secret length
        byte[] secretBytes = Files.readAllBytes(Paths.get(base64File));
        int secretLengthBytes = secretBytes.length;

        // Step 2: Auto-choose chunk size
        int chunkSizeBytes;
        if (secretLengthBytes <= 32 || keySize <= 256)
            chunkSizeBytes = 32; // 256-bit
        else if (secretLengthBytes <= 64 || keySize <= 512)
            chunkSizeBytes = 64; // 512-bit
        else if (secretLengthBytes <= 128 || keySize <= 1024)
            chunkSizeBytes = 128; // 1024-bit
        else
            chunkSizeBytes = 256; // 2048-bit

        System.out.println("Chunk size bytes: " + chunkSizeBytes);
        int totalChunks = (int) Math.ceil((double) base64Encoded.length() / chunkSizeBytes);
        System.out.println("Total Chunks : " + totalChunks);
        System.out.println("chunkSizeBytes : " + chunkSizeBytes);
        // Split and save chunks
        for (int i = 0; i < totalChunks; i++) {
            int start = i * chunkSizeBytes;
            int end = Math.min(start + chunkSizeBytes, base64Encoded.length());
            String chunk = base64Encoded.substring(start, end);
            String chunkFile = "processed/" + filename + "_g" + (i + 1) + ".txt";
            Files.writeString(Paths.get(chunkFile), chunk);
        }

        if (keySize == 0) {
            // Run Shamir SSS on each chunk
            System.out.println("Choosing simple path without keysize");
            ShamirSplitter.splitChunks(filename, totalChunks, k, n);

        } else {
            // Run Shamir with prime on each chunk
            System.out.println("Choosing simple path with keysize:" + keySize);
            ShamirPrimeSplitter.splitChunksWithPrime(filename, keySize, totalChunks, k, n);
        }

    }
}
