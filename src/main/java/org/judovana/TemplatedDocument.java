package org.judovana;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TemplatedDocument {
    private static final String metaInfDir = "META-INF";
    private static final String picturesDir = "Pictures";
    private static final String metaInfmanifestName = "manifest.xml";
    private static final String contextXmlName = "content.xml";
    private static final String stylesXmlName = "styles.xml";
    private static final String pageTemplateName = "page.xml";

    private String metaInfmanifest;
    private final String contextXml;
    private final String stylesXml;
    private final String pageTemplate;
    private final String id;
    private final List<ImageWrapper> images = new ArrayList<>();

    public TemplatedDocument(String id) throws IOException {
        this.id = id;
        final String loadDir = "org/judovana/tempaltes/" + id + ".x";
        contextXml = readTemplate(loadDir, contextXmlName);
        stylesXml = readTemplate(loadDir, stylesXmlName);
        pageTemplate = readTemplate(loadDir, pageTemplateName);
        metaInfmanifest = readTemplate(loadDir+"/"+metaInfDir, metaInfmanifestName);

    }

    private String readTemplate(String loadDir, String contextXmlName) throws IOException {
        try(InputStream in = this.getClass().getClassLoader().getResourceAsStream(loadDir + "/" + contextXmlName)){
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }


    public ImageWrapper add(String path, int width, int height) {
        ImageWrapper i = new ImageWrapper(new File(path), width, height);
        images.add(i);
        return i;
    }

    private static void printRatios(ImageWrapper i) {
        System.out.println(i.getWidth() + " x " + i.getHeight());
        System.out.println(i.getLandscapeRatio());
        System.out.println(i.getPortraitRatio());
    }

    public void generate() throws IOException {
        //String s=createDir();
        //System.out.println(s);
        //new File(s).deleteOnExit();
        String f = compress();
        System.out.println(f);
    }

    private String compress() throws IOException {
        File f = new File(id + ".odt");
        try(ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f))) {
            stringAsEntry(out, metaInfDir + "/" + metaInfmanifestName, metaInfmanifest);
            stringAsEntry(out, stylesXmlName, stylesXml);
            StringBuilder pages = new StringBuilder();
            for (ImageWrapper image : images) {
                imageAsEntry(out, image.getPath());
                pages.append(getPage(image));
            }
            stringAsEntry(out, contextXmlName, contextXml.replace("%{NEXT_PAGE}", pages));
        }
        return f.getAbsolutePath();
    }

    private void stringAsEntry(ZipOutputStream out, String name, String content) throws IOException {
        ZipEntry e = new ZipEntry(name);
        out.putNextEntry(e);
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        out.write(data, 0, data.length);
        out.closeEntry();
    }

    private void imageAsEntry(ZipOutputStream out, File f) throws IOException {
        ZipEntry e = new ZipEntry(picturesDir+"/"+f.getName());
        out.putNextEntry(e);
        byte[] data = Files.readAllBytes(f.toPath());
        out.write(data, 0, data.length);
        out.closeEntry();
    }

    private String createDir() throws IOException {
        Path target = Files.createTempDirectory(id);
        File metaInfTargetDir=new File(target.toString()+"/" + metaInfDir);
        metaInfTargetDir.mkdir();
        File picturesTargetDir=new File(target.toString()+"/" + picturesDir);
        picturesTargetDir.mkdir();
        Files.writeString(new File(metaInfTargetDir, metaInfmanifestName).toPath(), metaInfmanifest);
        Files.writeString(new File(target + "/" + stylesXmlName).toPath(), stylesXml);
        StringBuilder pages = new StringBuilder();
        for (ImageWrapper image: images) {
            Files.copy(image.getPath().toPath(), new File(picturesTargetDir, image.getPath().getName()).toPath());
            pages.append(getPage(image));
        }
        Files.writeString(new File(target + "/" + contextXmlName).toPath(), contextXml.replace("%{NEXT_PAGE}", pages));
        return target.toString();
    }

    private String getPage(ImageWrapper image) {
        String r = pageTemplate.
                replace("${IMAGE_NAME}", image.getPath().getName()).
                replace("First line", image.getPath().getName()).
                replace("Sescond line", image.getPath().getName()).
                replaceAll("--abs", "--abs of " + image.getWidth()+" x " + image.getHeight());
        if (id.equals("portrait")) {
            int h = 90;
            int w = (int) ((double) h * image.getPortraitRatio());
            System.out.println(image.getPath() + "  " + h + "   " + w);
            r = r.replace("style:rel-width=\"45", "style:rel-width=\"" + w);
            r = r.replace("style:rel-height=\"90", "style:rel-height=\"" + h);
        } else if (id.equals("landscape")) {
            int w = 90;
            int h = (int) ((double) w * image.getLandscapeRatio());
            System.out.println(image.getPath() + "  " + h + "   " + w);
            r = r.replace("style:rel-height=\"45", "style:rel-height=\"" + h);
            r = r.replace("style:rel-width=\"90", "style:rel-width=\"" + w);
        } else {
            throw new RuntimeException("unknown orientation " + id);
        }
        return r;
    }
}

