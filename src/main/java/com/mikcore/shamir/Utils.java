package com.mikcore.shamir;

import java.math.BigInteger;
import java.util.List;

public class Utils {

    public static BigInteger evaluatePolynomial(List<BigInteger> coefficients, BigInteger x) {
        BigInteger result = BigInteger.ZERO;
        BigInteger xPow = BigInteger.ONE;

        for (BigInteger coef : coefficients) {
            result = result.add(coef.multiply(xPow));
            xPow = xPow.multiply(x);
        }

        return result;
    }

    // Secure polynomial evaluation mod prime
    public static BigInteger evaluatePolynomialMod(List<BigInteger> coefficients, BigInteger x, BigInteger prime) {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < coefficients.size(); i++) {
            BigInteger term = coefficients.get(i).multiply(x.modPow(BigInteger.valueOf(i), prime)).mod(prime);
            result = result.add(term).mod(prime);
        }
        return result;
    }
}

