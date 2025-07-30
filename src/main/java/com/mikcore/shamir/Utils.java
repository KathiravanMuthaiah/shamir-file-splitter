package com.mikcore.shamir;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Utils {

    // Secure polynomial evaluation mod prime
    public static BigInteger evaluatePolynomialMod(List<BigInteger> coefficients, BigInteger x, BigInteger prime) {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < coefficients.size(); i++) {
            BigInteger term = coefficients.get(i).multiply(x.modPow(BigInteger.valueOf(i), prime)).mod(prime);
            result = result.add(term).mod(prime);
        }
        return result;
    }

    public static String sha256Hex(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}

