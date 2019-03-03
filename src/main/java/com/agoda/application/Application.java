package com.agoda.application;

import com.agoda.application.domain.ApplicationArguments;
import com.agoda.application.parsers.ApplicationArgumentParser;
import com.agoda.downloading.downloaders.DownloaderFactoryImpl;
import com.agoda.downloading.services.ResourceDownloaderService;
import com.agoda.filesoperations.services.FileServiceFactory;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.isNull;

@Slf4j
public class Application {

    public void run(String[] args) {
        try {
            ApplicationArgumentParser applicationArgumentParser = new ApplicationArgumentParser();
            ApplicationArguments applicationArguments = applicationArgumentParser.parseCommandLineArguments(args);

            if (isNull(applicationArguments)) {
                log.error("Application arguments is invalid");
                return;
            }

            ResourceDownloaderService resourceDownloader = new ResourceDownloaderService(FileServiceFactory.getFileService(), new DownloaderFactoryImpl());

            resourceDownloader.download(applicationArguments.getFolderPath(), applicationArguments.getResources(), Boolean.parseBoolean(applicationArguments.getIgnoreWrongResource()));

            resourceDownloader.close();
        } catch (Throwable throwable) {
            log.error("Exception occurred during execution", throwable);
        }
    }

}
