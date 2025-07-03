package org.judovana;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.imageio.ImageIO;


public class App {

    private final TemplatedDocument portrait;
    private final TemplatedDocument landscape;

    public static void main(String[] args) throws IOException {
        new App().work();
    }

    public App() throws IOException {
        portrait = new TemplatedDocument("portrait");
        landscape = new TemplatedDocument("landscape");
    }

    public void work() throws IOException {
        List<String> all = Files.readAllLines(new File("imagesToTryInOfficeGenerator").toPath());
        int i = 0;
        int e = 0;
        int l = 0;
        int p = 0;
        for (String path : all) {
            i++;
            if (path.startsWith("#")) {
                System.out.println(i + "/" + all.size() + " skipped " + path);
            } else {
                System.out.println(i + "/" + all.size() + " reading " + path);
                BufferedImage img = ImageIO.read(new File(path));
                if (img == null) {
                    e++;
                    System.out.println("  unknown error! " + e);
                    continue;
                }
                if (img.getWidth() > img.getHeight()) {
                    l++;
                    System.out.println("  landscape " + l);
                    landscape.add(path, img.getWidth(), img.getHeight());
                } else {
                    p++;
                    System.out.println("  portrait " + p);
                    portrait.add(path, img.getWidth(), img.getHeight());
                }
            }
        }
        System.out.println("landscapes: " + l);
        System.out.println("portraits: " + p);
        System.out.println("errors: " + e);
        System.out.println("generating: " + e);
        portrait.generate();
        landscape.generate();
    }
}

