package com.eatthepath.phash;

import org.jtransforms.dct.FloatDCT_2D;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class PerceptualHash {

    private static final int SCALED_IMAGE_SIZE = 32;
    private static final FloatDCT_2D DCT = new FloatDCT_2D(SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE);

    public static long getPerceptualHash(final File imageFile) throws IOException {
        return getPerceptualHash(ImageIO.read(imageFile));
    }

    public static long getPerceptualHash(final ImageInputStream imageInputStream) throws IOException {
        return getPerceptualHash(ImageIO.read(imageInputStream));
    }

    public static long getPerceptualHash(final InputStream imageInputStream) throws IOException {
        return getPerceptualHash(ImageIO.read(imageInputStream));
    }

    public static long getPerceptualHash(final URL imageUrl) throws IOException {
        return getPerceptualHash(ImageIO.read(imageUrl));
    }

    public static long getPerceptualHash(final Image image) {
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

        float lowFrequencyDctAverage = -dct[0];

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
