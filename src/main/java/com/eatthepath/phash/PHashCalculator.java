package com.eatthepath.phash;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PHashCalculator {

    private static final int SCALED_IMAGE_SIZE = 32;

    private static final double[][] DCT_COEFFICIENTS;

    static {
        DCT_COEFFICIENTS = new double[8][SCALED_IMAGE_SIZE];

        for (int k = 0; k < 8; k++) {
            for (int n = 0; n < SCALED_IMAGE_SIZE; n++) {
                DCT_COEFFICIENTS[k][n] = Math.cos((Math.PI / SCALED_IMAGE_SIZE) * k * (n + 0.5));
            }
        }
    }

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
                for (int k = 0; k < 8; k++) {
                    for (int x = 0; x < SCALED_IMAGE_SIZE; x++) {
                        dct[k + (y * SCALED_IMAGE_SIZE)] += pixels[x + (y * SCALED_IMAGE_SIZE)] * DCT_COEFFICIENTS[k][x];
                    }
                }
            }

            for (int x = 0; x < 8; x++) {
                for (int k = 0; k < 8; k++) {
                    for (int y = 0; y < SCALED_IMAGE_SIZE; y++) {
                        dct[x + (k * SCALED_IMAGE_SIZE)] += dct[x + (y * SCALED_IMAGE_SIZE)] * DCT_COEFFICIENTS[k][y];
                    }
                }
            }
        }

        double lowFrequencyDctAverage = -dct[0];

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                System.out.println(dct[x + (y * SCALED_IMAGE_SIZE)]);
                lowFrequencyDctAverage += dct[x + (y * SCALED_IMAGE_SIZE)];
            }
        }

        lowFrequencyDctAverage /= 64;

        long hash = 0;

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                hash <<= 1;

                if (dct[x + (y * SCALED_IMAGE_SIZE)] > lowFrequencyDctAverage) {
                    hash |= 1;
                }
            }
        }

        return hash;
    }
}
