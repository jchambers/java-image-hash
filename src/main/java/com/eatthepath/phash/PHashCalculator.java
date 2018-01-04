package com.eatthepath.phash;

import org.jtransforms.dct.FloatDCT_2D;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PHashCalculator {

    private static final int SCALED_IMAGE_SIZE = 32;

    private static final FloatDCT_2D DCT = new FloatDCT_2D(SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE);

    public static long calculateHash(final BufferedImage image) {
        final BufferedImage scaledImage = new BufferedImage(SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE, BufferedImage.TYPE_BYTE_GRAY);
        {
            final Graphics2D graphics = scaledImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            graphics.drawImage(image, 0, 0, SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE, null);

            graphics.dispose();
        }

        final float[] dct = new float[SCALED_IMAGE_SIZE * SCALED_IMAGE_SIZE];
        scaledImage.getData().getPixels(0, 0, SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE, dct);
        DCT.forward(dct, false);

        double lowFrequencyDctAverage = -dct[0];

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
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
