package org.judovana;

import java.io.File;

public class ImageWrapper {
    private final File path;
    private final int width;
    private final int height;

    public ImageWrapper(File path, int width, int height) {
        this.path = path;
        this.width = width;
        this.height = height;
    }

    public double getLandscapeRatio() {
        return (double)height/(double)width;
    }


    public double getPortraitRatio() {
        return (double)width/(double)height;
    }

    public int getHeight() {
        return height;
    }

    public File getPath() {
        return path;
    }

    public int getWidth() {
        return width;
    }
}
