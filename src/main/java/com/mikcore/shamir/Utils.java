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
}

