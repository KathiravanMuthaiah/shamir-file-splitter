package com.mikcore.shamir;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ShamirDecrypt {

    // Data structure to hold parsed share
    static class ShareFile {
        String originalFile;
        String hash;
        int chunkCount;
        BigInteger prime;
        BigInteger x;
        List<BigInteger> yList;
        Path filePath;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java ShamirDecryptor <decryptFolder> <k>");
            System.exit(1);
        }

        String decryptFolder = args[0];
        int k = Integer.parseInt(args[1]);
        String originalFile = null;
        String originalHash = null;
        // Ensure reconstructed folder exists
        Files.createDirectories(Paths.get("reconstructed"));

        List<ShareFile> shares = loadShares(decryptFolder);

        if (shares.isEmpty()) {
            System.out.println("No valid share files found in: " + decryptFolder);
            return;
        }

        Map<Integer, byte[]> reconstructedChunks = new TreeMap<>();

        if (shares.size() < k) {
            System.out.println(": Not enough shares (" +
                    shares.size() + "/" + k + ")");
            System.exit(1);
        }

        // Take first K shares
        List<ShareFile> selectedShares = shares.subList(0, k);

        BigInteger prime = selectedShares.get(0).prime;
        // Save Base64 intermediate chunk for inspection
        originalFile = selectedShares.get(0).originalFile;
        originalHash = selectedShares.get(0).hash;
        int totalChunks = selectedShares.get(0).chunkCount;

        for (int i = 0; i < totalChunks; i++) {
            // Extract x, y for interpolation
            List<BigInteger> xs = new ArrayList<>();
            List<BigInteger> ys = new ArrayList<>();
            for (ShareFile shareFile : selectedShares) {
                System.out.println("\tx\t"+shareFile.x);
                System.out.println("\ty\t"+shareFile.yList.get(i));
                xs.add(shareFile.x);
                ys.add(shareFile.yList.get(i));
            }
            BigInteger secret = lagrangeInterpolation(BigInteger.ZERO, xs, ys, prime);

            System.out.println("Sectre:" + secret + " decrypt/" + originalFile + "_g" + i + ".txt");

            // Convert to bytes
            byte[] secretBytes = secret.toByteArray();

            // 1️⃣ Remove possible BigInteger sign-padding leading zero
            if (secretBytes[0] == 0) {
                secretBytes = Arrays.copyOfRange(secretBytes, 1, secretBytes.length);
            }

            // 2️⃣ Convert to string (safe ASCII)
            String base64Chunk = new String(secretBytes, StandardCharsets.US_ASCII);

            // 3️⃣ Decode back to original binary chunk
            byte[] originalChunk = Base64.getDecoder().decode(base64Chunk);

            System.out.println("reconstructed" + originalFile + "_g" + i + ":" + base64Chunk);

            Path base64Out = Paths.get("reconstructed",
                    originalFile + "_g" + i + ".txt");
            Files.writeString(base64Out, base64Chunk);

            reconstructedChunks.put(i, originalChunk);

            System.out.println("✅ Reconstructed chunk: " + i +
                    " using " + k + " shares. Base64 saved: " + base64Out);

        }
        // Merge all chunks by sorted chunk index
        if (!reconstructedChunks.isEmpty()) {
            Path outputFile = Paths.get("reconstructed", "reconstructed_" + originalFile);

            try (FileOutputStream fos = new FileOutputStream(outputFile.toFile())) {
                for (int key : reconstructedChunks.keySet()) {
                    fos.write(reconstructedChunks.get(key));
                }
            }

            // Verify hash
            byte[] reconstructedData = Files.readAllBytes(outputFile);
            String reconstructedHash = sha256Hex(reconstructedData);

            System.out.println("\n=== Reconstruction Complete ===");
            System.out.println("Output File: " + outputFile);
            System.out.println("Original Hash: " + originalHash);
            System.out.println("Reconstructed Hash: " + reconstructedHash);

            if (originalHash.equals(reconstructedHash)) {
                System.out.println("✅ Integrity verified successfully!");
            } else {
                System.out.println("❌ Hash mismatch: Reconstruction may be corrupted.");
            }
        }

    }

    // Load share files and parse metadata
    private static List<ShareFile> loadShares(String folder) throws IOException {
        List<ShareFile> shares = new ArrayList<>();
        Files.walk(Paths.get(folder))
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        System.out.println(path);
                        List<String> lines = Files.readAllLines(path);
                        if (lines.size() < 2)
                            return;
                        System.out.println(path+"\tLineSize:"+lines.size());
                        ShareFile sf = new ShareFile();
                        sf.originalFile = lines.get(0).split(": ")[1].trim();
                        sf.hash = lines.get(1).split(": ")[1].trim();
                        sf.x = new BigInteger((lines.get(2).split(": ")[1].trim()));
                        sf.chunkCount = Integer.parseInt((lines.get(3).split(": ")[1].trim()));
                        sf.prime = new BigInteger(lines.get(4).split(": ")[1].trim());
                        System.out.println(path+"\tsf.x"+sf.x);
                        sf.yList = new ArrayList<>();
                        for (int i = 0; i < sf.chunkCount; i++) {
                            System.out.println(path+"\tsf.chunkCount"+sf.chunkCount);
                            System.out.println(path+"\ty\t"+new BigInteger(lines.get(i+5).trim()));
                            sf.yList.add(new BigInteger(lines.get(i+5).trim()));
                        }
                        sf.filePath = path;

                        shares.add(sf);
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                });
        return shares;
    }

    // Lagrange interpolation at x=0
    private static BigInteger lagrangeInterpolation(BigInteger x, List<BigInteger> xs, List<BigInteger> ys,
            BigInteger prime) {
        BigInteger result = BigInteger.ZERO;
        int k = xs.size();
            
        for (int i = 0; i < k; i++) {
            BigInteger xi = xs.get(i);
            BigInteger yi = ys.get(i);
            System.out.println(xi+", "+yi);
            BigInteger li = BigInteger.ONE;
            for (int j = 0; j < k; j++) {
                if (i == j)
                    continue;

                BigInteger xj = xs.get(j);
                BigInteger numerator = x.subtract(xj);
                BigInteger denominator = xi.subtract(xj);

                numerator = numerator.mod(prime);
                denominator = denominator.mod(prime);
                BigInteger invDenominator = denominator.modInverse(prime);
                li = li.multiply(numerator).mod(prime).multiply(invDenominator).mod(prime);

            }

            result = result.add(yi.multiply(li)).mod(prime);

        }
        return result;
    }

    private static String sha256Hex(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}