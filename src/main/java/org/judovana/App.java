package org.judovana;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        List<String> all = Files.readAllLines(new File("imagesToTryInOfficeGenerator").toPath());
        int i = 0;
        int e = 0;
        int l=0;
        int p=0;
        for (String path: all) {
            i++;
            if (path.startsWith("#")) {
                System.out.println(i + "/"+all.size() + " skipped " + path);
            } else {
                System.out.println(i + "/"+all.size() + " reading " + path);
                BufferedImage img = ImageIO.read(new File(path));
                if (img == null) {
                    e++;
                    System.out.println("  unknown error! " + e);
                    continue;
                }
                if (img.getWidth()>img.getHeight()) {
                    l++;
                    System.out.println("  landscape " + l);
                } else {
                    p++;
                    System.out.println("  portrait " + p);
                }
            }
        }
        System.out.println("landscapes: " + l);
        System.out.println("portraits: " + p);
        System.out.println("errors: " + e);
    }
}
