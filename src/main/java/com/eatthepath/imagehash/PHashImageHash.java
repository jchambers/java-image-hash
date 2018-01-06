package com.eatthepath.imagehash;

import org.jtransforms.dct.FloatDCT_2D;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * <p>This class calculates <a href="https://en.wikipedia.org/wiki/Perceptual_hashing">perceptual hashes</a> of images
 * using an adaptation of the <a href="http://www.phash.org/">pHash</a> algorithm. Callers can evaluate the similarity
 * of images by calculating the <a href="https://en.wikipedia.org/wiki/Hamming_distance">Hamming distance</a> between
 * the hashes of those images. For example:</p>
 *
 * {@code int difference = Long.bitCount(firstHash ^ secondHash);}
 */
class PHashImageHash implements PerceptualImageHash {

    private static final int SCALED_IMAGE_SIZE = 32;
    private static final FloatDCT_2D DCT = new FloatDCT_2D(SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE);

    public long getPerceptualHash(final File imageFile) throws IOException {
        return this.getPerceptualHash(ImageIO.read(imageFile));
    }

    public long getPerceptualHash(final ImageInputStream imageInputStream) throws IOException {
        return this.getPerceptualHash(ImageIO.read(imageInputStream));
    }

    public long getPerceptualHash(final InputStream inputStream) throws IOException {
        return this.getPerceptualHash(ImageIO.read(inputStream));
    }

    public long getPerceptualHash(final URL imageUrl) throws IOException {
        return this.getPerceptualHash(ImageIO.read(imageUrl));
    }

    public long getPerceptualHash(final Image image) {
        // As a foreword, this is an adaptation of a high-level explanation of the pHash algorithm. Many implementation
        // details are probably a bit off the mark. For the rough explanation, see
        // http://www.hackerfactor.com/blog/index.php?/archives/432-Looks-Like-It.html.

        // Start by rescaling the image to a known size. This may involve some squishing
        // (or, in rare cases, stretching), but that's fine for our purposes. We also want to go to greyscale so we only
        // have a single channel to worry about.
        final BufferedImage scaledImage = new BufferedImage(SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE, BufferedImage.TYPE_BYTE_GRAY);
        {
            final Graphics2D graphics = scaledImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            graphics.drawImage(image, 0, 0, SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE, null);

            graphics.dispose();
        }

        // Next, we want to calculate the 2D discrete cosine transform for our small image.
        final float[] dct = new float[SCALED_IMAGE_SIZE * SCALED_IMAGE_SIZE];
        scaledImage.getData().getPixels(0, 0, SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE, dct);
        DCT.forward(dct, false);

        // We're interested in the lowest-frequency parts of the DCT, so we take the lowest 8x8 square of frequency
        // components. The lowest-frequency bits tend to carry the most "structural" information about a signal (in
        // some ways, they're analogous to the most significant bits of an integer), and so they're also the least
        // sensitive to smaller perturbations in an image. For quantization purposes, we want to calculate the average
        // of the low-frequency components excluding the zero-frequency ("DC") component, which is often an outlier.
        float lowFrequencyDctAverage = -dct[0];

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                lowFrequencyDctAverage += dct[x + (y * SCALED_IMAGE_SIZE)];
            }
        }

        lowFrequencyDctAverage /= 64;

        // Now that we have an average value for the lowest-frequency components, we can quantize each of the components
        // in that 8x8 square into a 1 or 0 depending on whether the component is above or below the average value for
        // that square. Each of those components turns into a bit in our finished hash.
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
