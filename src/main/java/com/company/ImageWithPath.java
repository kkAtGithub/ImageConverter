package com.company;

import java.awt.image.BufferedImage;

public class ImageWithPath {
    private final String imagePath;
    private final BufferedImage image;

    ImageWithPath(String imagePath, BufferedImage image) {
        this.imagePath = imagePath;
        this.image = image;
    }

    public String getImagePath() {
        return imagePath;
    }

    public BufferedImage getImage() {
        return image;
    }
}