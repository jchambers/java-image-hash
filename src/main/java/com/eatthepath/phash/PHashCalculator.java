package com.eatthepath.phash;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PHashCalculator {

    private static final int SCALED_IMAGE_SIZE = 32;
    private static final int DCT_AVERAGE_SIZE = SCALED_IMAGE_SIZE / 4;

    public static long calculateHash(final BufferedImage image) {
        final BufferedImage scaledImage = new BufferedImage(SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE, BufferedImage.TYPE_BYTE_GRAY);
        {
            final Graphics2D graphics = scaledImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            graphics.drawImage(image, 0, 0, SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE, null);

            graphics.dispose();
        }

        final double[] dct = new double[SCALED_IMAGE_SIZE * SCALED_IMAGE_SIZE];
        {
            final double[] pixels = new double[SCALED_IMAGE_SIZE * SCALED_IMAGE_SIZE];
            scaledImage.getData().getPixels(0, 0, SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE, pixels);

            for (int y = 0; y < SCALED_IMAGE_SIZE; y++) {
                for (int x = 0; x < SCALED_IMAGE_SIZE; x++) {
                    System.out.format("%d,", (int) pixels[x + (y * SCALED_IMAGE_SIZE)]);
                }

                System.out.format(";");
            }

            System.out.format("\n");

            final double PI_N = Math.PI / SCALED_IMAGE_SIZE;

            for (int j = 0; j < SCALED_IMAGE_SIZE; j++) {
                for (int k = 0; k < SCALED_IMAGE_SIZE; k++) {
                    for (int x = 0; x < SCALED_IMAGE_SIZE; x++) {
                        double xComponent = Math.cos(PI_N * j * (x + 0.5));

                        for (int y = 0; y < SCALED_IMAGE_SIZE; y++) {
                            dct[j + (k * SCALED_IMAGE_SIZE)] +=
                                    pixels[x + (y * SCALED_IMAGE_SIZE)] * xComponent * Math.cos(PI_N * k * (y + 0.5));
                        }
                    }
                }
            }
        }

        double lowFrequencyDctAverage = -dct[0];

        for (int x = 0; x < DCT_AVERAGE_SIZE; x++) {
            for (int y = 0; y < DCT_AVERAGE_SIZE; y++) {
                lowFrequencyDctAverage += dct[x + (y * SCALED_IMAGE_SIZE)];
            }
        }

        lowFrequencyDctAverage /= (DCT_AVERAGE_SIZE * DCT_AVERAGE_SIZE);

        long hash = 0;

        for (int y = 0; y < 8; y++) {
            byte b = 0;

            for (int x = 0; x < 8; x++) {
                if (dct[x + (y * 8)] > lowFrequencyDctAverage) {
                    b |= (1 << x);
                }
            }

            hash <<= 8;
            hash |= (b & 0xff);
        }

        return hash;
    }
}
