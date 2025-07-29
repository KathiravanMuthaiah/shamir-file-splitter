package com.mikcore.shamir;

public class Main {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Usage: java Main <inputFilePath> <keySize> <thresholdK> <totalSharesN>");
            System.exit(1);
        }

        String inputPath = args[0];
        int keySize = Integer.parseInt(args[1]);
        int k = Integer.parseInt(args[2]);
        int n = Integer.parseInt(args[3]);

        try {
            FileSplitter.processInputFile(inputPath,keySize, k, n);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }
}
