package com.mikcore.shamir;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java Main <inputFilePath> <thresholdK> <totalSharesN>");
            System.exit(1);
        }

        String inputPath = args[0];
        int k = Integer.parseInt(args[1]);
        int n = Integer.parseInt(args[2]);

        try {
            FileSplitter.processInputFile(inputPath, k, n);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }
}
