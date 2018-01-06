package com.eatthepath.imagehash;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface PerceptualImageHash {

    /**
     * Calculates a perceptual hash of the image loaded from the given file.
     *
     * @param imageFile the file from which to load the image to be hashed
     *
     * @return a 64-bit perceptual hash of the image in the given file
     *
     * @throws IOException if an image could not be loaded from the given file for any reason
     */
     long getPerceptualHash(final File imageFile) throws IOException;

    /**
     * Calculates a perceptual hash of the image loaded from the given image input stream.
     *
     * @param imageInputStream the image input stream from which to load the image to be hashed
     *
     * @return a 64-bit perceptual hash of the image in the given image input stream
     *
     * @throws IOException if an image could not be loaded from the given image input stream for any reason
     */
     long getPerceptualHash(final ImageInputStream imageInputStream) throws IOException;

    /**
     * Calculates a perceptual hash of the image loaded from the given input stream.
     *
     * @param inputStream the input stream from which to load the image to be hashed
     *
     * @return a 64-bit perceptual hash of the image in the given input stream
     *
     * @throws IOException if an image could not be loaded from the given input stream for any reason
     */
     long getPerceptualHash(final InputStream inputStream) throws IOException;

    /**
     * Calculates a perceptual hash of the image loaded from the given URL.
     *
     * @param imageUrl the URL from which to load the image to be hashed
     *
     * @return a 64-bit perceptual hash of the image at the given URL
     *
     * @throws IOException if an image could not be loaded from the given URL for any reason
     */
     long getPerceptualHash(final URL imageUrl) throws IOException;

    /**
     * Calculates a perceptual hash of the given image.
     *
     * @param image the image to be hashed
     *
     * @return a 64-bit perceptual hash of the given image
     */
    long getPerceptualHash(final Image image);
}
