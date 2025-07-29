package com.mikcore.shamir;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.*;

public class ShamirSplitter {

    public static void splitChunks(String filename, int totalChunks, int k, int n) throws IOException {
        SecureRandom secureRandom = new SecureRandom();

        for (int chunkIdx = 1; chunkIdx <= totalChunks; chunkIdx++) {
            String chunkFile = "processed/" + filename + "_g" + chunkIdx + ".txt";
            String base64Chunk = Files.readString(Paths.get(chunkFile));
            BigInteger secret = new BigInteger(base64Chunk.getBytes());

            // Generate random coefficients
            List<BigInteger> coefficients = new ArrayList<>();
            coefficients.add(secret);
            for (int i = 1; i < k; i++) {
                coefficients.add(new BigInteger(secret.bitLength() + 8, secureRandom));
            }

            // Create output folders
            for (int i = 1; i <= n; i++) {
                Files.createDirectories(Paths.get("output/" + i));
            }

            // Generate and save shares
            for (int i = 1; i <= n; i++) {
                BigInteger x = BigInteger.valueOf(i);
                BigInteger y = Utils.evaluatePolynomial(coefficients, x);

                String shareFile = String.format("output/%d/%s_g%d_n%d.txt", i, filename, chunkIdx, i);
                Files.writeString(Paths.get(shareFile), x + "," + y + "," + "-1");
            }
        }

        System.out.println("All shares written to output folders.");
    }
}

