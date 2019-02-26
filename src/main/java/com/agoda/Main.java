package com.agoda;

import com.agoda.loader.services.FileService;
import com.agoda.loader.services.ResourceDownloaderService;
import org.apache.commons.cli.*;

public class Main {

    public static final String FOLDER_PATH = "folder path";
    public static final String RESOURCES_LIST = "resources list";
    public static final String FOLDER_PATH_OPTION = "f";
    public static final String RESOURCE_LIST_OPTION = "r";

    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(FOLDER_PATH_OPTION, true, FOLDER_PATH);
        options.addOption(RESOURCE_LIST_OPTION, true, RESOURCES_LIST);

        CommandLineParser parser = new DefaultParser();

        CommandLine cmd;
        try {
            cmd = parser.parse(options, args, true);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("loader", options);
            return;
        }

        String folderPath = cmd.getOptionValue(FOLDER_PATH_OPTION);
        String recourseList = cmd.getOptionValue(RESOURCE_LIST_OPTION);

        ResourceDownloaderService resourceDownloader = new ResourceDownloaderService(new FileService());
        resourceDownloader.download(folderPath, recourseList.split(","));

//        DefaultFileWriter httpLoader = new DefaultFileWriter("/Users/viktor.sobol/Viktor/tech_task/test.txt");
//        httpLoader.load(new URL("ftp://ftp.denx.de/pub/u-boot/u-boot-2011.12.tar.bz2"));
    }
}
