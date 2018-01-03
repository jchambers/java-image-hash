package com.eatthepath.phash;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ExampleApp {

    public static void main(final String... args) throws IOException {
        final BufferedImage image = ImageIO.read(new File("/Users/jon/Desktop/test-image.png"));
        // final BufferedImage modifiedImage = ImageIO.read(new File("/Users/jon/Desktop/test-image-modified.png"));
        final BufferedImage modifiedImage = ImageIO.read(new File("/Users/jon/Desktop/fax-machine.jpg"));

        final long hash = PHashCalculator.calculateHash(image);
        final long modifiedHash = PHashCalculator.calculateHash(modifiedImage);

        System.out.format("0x%016X\n", hash);
        System.out.format("0x%016X\n", modifiedHash);

        System.out.println(Long.bitCount(hash ^ modifiedHash));
    }
}
