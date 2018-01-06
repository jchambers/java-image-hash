package com.eatthepath.imagehash;

public class PerceptualImageHashes {

    private static final PHashImageHash P_HASH_IMAGE_HASH = new PHashImageHash();
    private static final AverageHashImageHash AVERAGE_HASH_IMAGE_HASH = new AverageHashImageHash();

    private PerceptualImageHashes() {
        // A private constructor prevents callers from accidentally instantiating this class.
    }

    public static PerceptualImageHash getPHashImageHash() {
        return P_HASH_IMAGE_HASH;
    }

    public static PerceptualImageHash getAverageHashImageHash() {
        return AVERAGE_HASH_IMAGE_HASH;
    }
}
