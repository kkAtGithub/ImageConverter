package com.company;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wind
 */
public class Main {
    private static final String INPUT_DIRECTORY = "\\input\\";
    private static final String OUTPUT_DIRECTORY = "\\output\\";
    private static final String OUTPUT_FILE_FORMAT = "webp";



    private static boolean isExists(String targetPath, boolean createIfNotExists) {
        File target = new File(targetPath);
        if (target.exists()) {
            return true;
        }else if (createIfNotExists) {
            return target.mkdirs();
        }else {
            return false;
        }
    }

    private static List<ImageWithPath> getNewImage(String workDirectory, Boolean wdIsRoot) {
        System.out.println(workDirectory);
        String inputPath;
        String outputPath;

        if (wdIsRoot) {
            inputPath = workDirectory + INPUT_DIRECTORY;
            isExists(inputPath, true);
            outputPath = workDirectory + OUTPUT_DIRECTORY;
        }else {
            inputPath = workDirectory;
            outputPath = workDirectory.replace(INPUT_DIRECTORY, OUTPUT_DIRECTORY);
        }
        isExists(outputPath, true);


        List<ImageWithPath> newImagesList = new ArrayList<>();

        File dirToSan = new File(inputPath);
        File[] fileList = dirToSan.listFiles();
        assert fileList != null;
        for (File entry:fileList) {

            if (entry.isDirectory()) {
                newImagesList.addAll(getNewImage(entry.getPath(), false));
            }else {
                try {
                    BufferedImage image = ImageIO.read(entry);
                    if (image != null) {
                        String entryName = entry.getName();
                        String outputImage = outputPath + '\\'
                                + entryName.substring(0, entryName.lastIndexOf('.')+1) + OUTPUT_FILE_FORMAT;
                        if (!isExists(outputImage, false)) {
                            newImagesList.add(new ImageWithPath(outputImage, image));
                        }
                    }
                } catch(IOException ignored) {
                }
            }
        }
        return newImagesList;
    }

    public static boolean convertTask(ImageWithPath imageToConvert) {
        File newConvertTask = new File(imageToConvert.getImagePath());
        // Obtain a WebP ImageWriter instance
        try {
            ImageIO.write(imageToConvert.getImage(), OUTPUT_FILE_FORMAT, newConvertTask);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static void main(String[] args) {
	// write your code here
        System.out.println("用户的当前工作目录:\n"+System.getProperty("user.dir"));
        List<ImageWithPath> newImagesList = getNewImage(System.getProperty("user.dir"), true);
        System.out.println(newImagesList);
        for (ImageWithPath entry:newImagesList){
            System.out.println(entry.getImagePath());
            System.out.println(entry.getImage());
            convertTask(entry);
        }
    }
}
