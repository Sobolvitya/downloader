package com.agoda.application.parsers;

import com.agoda.application.domain.ApplicationArguments;
import org.apache.commons.cli.*;

import static java.util.Arrays.asList;

public class ApplicationArgumentParser {

    private static final String FOLDER_PATH = "folder path";
    private static final String RESOURCES_LIST = "resources list";
    private static final String IGNORE_WRONG_RESOURCE = "ignore wrong resource";
    private static final String FOLDER_PATH_OPTION = "f";
    private static final String RESOURCE_LIST_OPTION = "r";
    private static final String IGNORE_WRONG_RESOURCE_OPTION = "i";

    public ApplicationArguments parseCommandLineArguments(String[] args) {
        Options options = buildOptions();

        CommandLineParser parser = new DefaultParser();

        CommandLine cmd;
        try {
            cmd = parser.parse(options, args, true);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("loader", options);
            return null;
        }

        String[] split = cmd.getOptionValue(RESOURCE_LIST_OPTION).split(",");

        return ApplicationArguments.builder()
                .folderPath(cmd.getOptionValue(FOLDER_PATH_OPTION))
                .resources(asList(split))
                .ignoreWrongResource(cmd.getOptionValue(IGNORE_WRONG_RESOURCE_OPTION))
                .build();
    }

    private Options buildOptions() {
        Options options = new Options();

        Option folderPathOption = new Option(FOLDER_PATH_OPTION, true, FOLDER_PATH);
        folderPathOption.setRequired(true);

        Option resourceListOption = new Option(RESOURCE_LIST_OPTION, true, RESOURCES_LIST);
        resourceListOption.setRequired(true);

        Option ignoreWrongResourceOption = new Option(IGNORE_WRONG_RESOURCE_OPTION, true, IGNORE_WRONG_RESOURCE);
        ignoreWrongResourceOption.setRequired(false);

        options.addOption(folderPathOption);
        options.addOption(resourceListOption);
        options.addOption(ignoreWrongResourceOption);
        return options;
    }
}
