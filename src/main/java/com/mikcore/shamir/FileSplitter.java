package com.mikcore.shamir;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class FileSplitter {

    public static void processInputFile(String inputPath, int keySize, int k, int n) throws Exception {
        SecureRandom secureRandom = new SecureRandom();

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

        byte[] originalContent = Files.readAllBytes(Paths.get(inputPath));
        String fileHash = null;
        try {
            fileHash = Utils.sha256Hex(originalContent);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Exception(e);
        }

        if (keySize != 256 && keySize != 512 && keySize != 1024 && keySize != 2048) {
            keySize = 2048;
        }
        BigInteger prime = BigInteger.probablePrime(keySize, secureRandom);
        for (int i = 1; i <= n; i++) {
            String shareFile = String.format("output/%s_n%d.txt", filename, i);
            try (PrintWriter pw = new PrintWriter(new FileWriter(shareFile))) {
                pw.println("# ORIGINAL_FILE: " + filename);
                pw.println("# ORIGINAL_HASH: " + fileHash);
                pw.println("# SHARE_INDEX: " + i);
                pw.println("# TOTAL_CHUNK_COUNT: " + totalChunks);
                pw.println("# PRIME: " + prime);
            }
        }

        // Split and save chunks
        for (int i = 0; i < totalChunks; i++) {
            int start = i * chunkSizeBytes;
            int end = Math.min(start + chunkSizeBytes, base64Encoded.length());
            String chunk = base64Encoded.substring(start, end);
            System.out.println("Sectre:" + chunk + " output/" + filename + "_chunkIdx: " + i);
            ShamirPrimeSplitter.splitChunksWithPrime(filename, fileHash, keySize, prime, chunk, k, n);
            String chunkFile = "processed/" + filename + "_g" + (i + 1) + ".txt";
            Files.writeString(Paths.get(chunkFile), chunk);
        }

        // Run Shamir with prime on each chunk
        // System.out.println("Choosing simple path with keysize:" + keySize);
        // ShamirPrimeSplitter.splitChunksWithPrime(filename, fileHash, keySize,
        // totalChunks, k, n);

    }
}
