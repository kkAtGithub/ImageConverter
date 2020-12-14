package com.company;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author wind
 */
public class Main {
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static String WORK_DIRECTORY;
    private static String FILE_PATH_DELIMITER = "/";
    private static String INPUT_DIRECTORY = "\\input\\";
    private static String OUTPUT_DIRECTORY = "\\output\\";
    private static String OUTPUT_FILE_FORMAT = "webp";
    private static final String DEFAULT_CONFIG = "INPUT_DIRECTORY=input\n" +
                                                    "OUTPUT_DIRECTORY=output\n" +
                                                    "OUTPUT_FILE_FORMAT=webp\n";



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
                        String outputImage = outputPath + FILE_PATH_DELIMITER
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

    private static boolean convertTask(ImageWithPath imageToConvert) {
        File newConvertTask = new File(imageToConvert.getImagePath());
        try {
            ImageIO.write(imageToConvert.getImage(), OUTPUT_FILE_FORMAT, newConvertTask);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void initialize() throws IOException {
        WORK_DIRECTORY = System.getProperty("user.dir");
        if (System.getProperty("os.name").contains("Windows")) {
            FILE_PATH_DELIMITER = "\\";
        }
        File propertiesFile = new File(CONFIG_FILE_NAME);
        if (!propertiesFile.exists()) {
            FileOutputStream genConfigFile = new FileOutputStream(propertiesFile);
            byte[] defaultConfToBytes = DEFAULT_CONFIG.getBytes();
            genConfigFile.write(defaultConfToBytes);
            genConfigFile.close();
        }
        Properties properties = new Properties();
        BufferedReader readConf = new BufferedReader(new FileReader(propertiesFile));
        properties.load(readConf);

        INPUT_DIRECTORY = properties.getProperty("INPUT_DIRECTORY");
        OUTPUT_DIRECTORY = properties.getProperty("OUTPUT_DIRECTORY");
        OUTPUT_FILE_FORMAT = properties.getProperty("OUTPUT_FILE_FORMAT");
    }


    public static void main(String[] args) {
	// write your code here
        try {
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<ImageWithPath> newImagesList = getNewImage(WORK_DIRECTORY, true);
        System.out.println(newImagesList);
        for (ImageWithPath entry:newImagesList){
            System.out.println(entry.getImagePath());
            System.out.println(entry.getImage());
            convertTask(entry);
        }
        System.out.println(INPUT_DIRECTORY);
        System.out.println(OUTPUT_DIRECTORY);
        System.out.println(OUTPUT_FILE_FORMAT);
    }
}
