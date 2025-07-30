package com.mikcore.shamir;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ShamirPrimeSplitter {
    public static void splitChunksWithPrime(String filename, String fileHash, int keySize,
            int totalChunks, int k, int n) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        for (int chunkIdx = 1; chunkIdx <= totalChunks; chunkIdx++) {
            String chunkFile = "processed/" + filename + "_g" + chunkIdx + ".txt";
            String base64Chunk = Files.readString(Paths.get(chunkFile));
            // 1️⃣ Base64 encode binary chunk first (already done outside)
            // 2️⃣ Convert Base64 string to bytes and then BigInteger
            byte[] base64Bytes = base64Chunk.getBytes(StandardCharsets.US_ASCII);
            BigInteger secret = new BigInteger(1, base64Bytes); // Use 1 for positive BigInteger
            System.out.println("Sectre:"+secret+" processed/" + filename + "_g" + chunkIdx + ".txt");
            BigInteger prime = BigInteger.probablePrime(keySize, secureRandom);
                    
            System.out.println("Chunk " + chunkIdx + " uses prime of " + keySize + " bits.");

            // 2️⃣ Generate random polynomial coefficients modulo prime
            List<BigInteger> coefficients = new ArrayList<>();
            BigInteger a0 = secret.mod(prime);
            coefficients.add(a0); // a0 = secret
            for (int i = 1; i < k; i++) {
                BigInteger coeff = new BigInteger(prime.bitLength() - 1, secureRandom).mod(prime);
                coefficients.add(coeff);
            }

            // 3️⃣ Prepare output folders
            for (int i = 1; i <= n; i++) {
                Files.createDirectories(Paths.get("output/" + i));
            }

            // 4️⃣ Generate n shares
            for (int i = 1; i <= n; i++) {
                BigInteger x = new BigInteger(prime.bitLength() - 1, secureRandom).add(BigInteger.ONE);
                BigInteger y = Utils.evaluatePolynomialMod(coefficients, x, prime);

                String shareFile = String.format("output/%d/%s_g%d_n%d.txt", i, filename, chunkIdx, i);
                try (PrintWriter pw = new PrintWriter(new FileWriter(shareFile))) {
                    pw.println("# ORIGINAL_FILE: " + filename);
                    pw.println("# ORIGINAL_HASH: " + fileHash);
                    pw.println("# CHUNK_INDEX: " + chunkIdx);
                    pw.println("# SHARE_INDEX: " + i);
                    pw.println("# PRIME: " + prime);
                    pw.println(x + "," + y);
                }
            }
        }

        System.out.println("All secure shares written to output folders.");
    }

    public static void splitChunksWithPrime(String filename, String fileHash,int keySize,BigInteger prime,
            String base64Chunk, int k, int n) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        

            // 1️⃣ Base64 encode binary chunk first (already done outside)
            // 2️⃣ Convert Base64 string to bytes and then BigInteger
            byte[] base64Bytes = base64Chunk.getBytes(StandardCharsets.US_ASCII);
            BigInteger secret = new BigInteger(1, base64Bytes); // Use 1 for positive BigInteger
            

            // 2️⃣ Generate random polynomial coefficients modulo prime
            List<BigInteger> coefficients = new ArrayList<>();
            BigInteger a0 = secret.mod(prime);
            coefficients.add(a0); // a0 = secret
            for (int i = 1; i < k; i++) {
                BigInteger coeff = new BigInteger(prime.bitLength() - 1, secureRandom).mod(prime);
                coefficients.add(coeff);
            }

            // 4️⃣ Generate n shares
            for (int i = 1; i <= n; i++) {
                BigInteger x = new BigInteger(prime.bitLength() - 1, secureRandom).add(BigInteger.ONE);
                BigInteger y = Utils.evaluatePolynomialMod(coefficients, x, prime);

               
            String shareFile = String.format("output/%s_n%d.txt", filename,i);
                try (PrintWriter pw = new PrintWriter(new FileWriter(shareFile,true))) {
                    pw.println(x + "," + y);
                }
            }
        

        System.out.println("All secure shares written to output folders.");
    }


}
