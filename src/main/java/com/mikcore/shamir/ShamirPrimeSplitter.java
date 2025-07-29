package com.mikcore.shamir;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ShamirPrimeSplitter {
     public static void splitChunksWithPrime(String filename,int keySize,int totalChunks, int k, int n) throws IOException {
        SecureRandom secureRandom = new SecureRandom();

        if(keySize!=256 || keySize !=512 || keySize !=1024 || keySize !=2048){
            keySize = 2048;
        }

        for (int chunkIdx = 1; chunkIdx <= totalChunks; chunkIdx++) {
            String chunkFile = "processed/" + filename + "_g" + chunkIdx + ".txt";
            String base64Chunk = Files.readString(Paths.get(chunkFile));
            BigInteger secret = new BigInteger(base64Chunk.getBytes());

            BigInteger prime = BigInteger.probablePrime(keySize, secureRandom);
            System.out.println("Chunk " + chunkIdx + " uses prime of " + keySize + " bits.");

            // 2️⃣ Generate random polynomial coefficients modulo prime
            List<BigInteger> coefficients = new ArrayList<>();
            coefficients.add(secret.mod(prime)); // a0 = secret
            for (int i = 1; i < k; i++) {
                BigInteger coeff = new BigInteger(keySize - 1, secureRandom).mod(prime);
                coefficients.add(coeff);
            }

            // 3️⃣ Prepare output folders
            for (int i = 1; i <= n; i++) {
                Files.createDirectories(Paths.get("output/" + i));
            }

            // 4️⃣ Generate n shares
            for (int i = 1; i <= n; i++) {
                BigInteger x = BigInteger.valueOf(i);
                BigInteger y = Utils.evaluatePolynomialMod(coefficients, x, prime);

                String shareFile = String.format("output/%d/%s_g%d_n%d.txt", i, filename, chunkIdx, i);
                Files.writeString(Paths.get(shareFile),
                        x.toString() + "," + y.toString() + "," + prime.toString());
            }
        }

        System.out.println("All secure shares written to output folders.");
    }
}
