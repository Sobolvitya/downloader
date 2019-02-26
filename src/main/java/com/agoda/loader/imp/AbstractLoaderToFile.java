package com.agoda.loader.imp;

import com.agoda.loader.services.FileService;
import com.agoda.loader.DownloadingResult;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

@Slf4j
abstract class AbstractLoaderToFile implements Loader {

    private final FileService fileService;

    protected AbstractLoaderToFile(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public DownloadingResult load(URL from, Path to) {

        DownloadingResult result = new DownloadingResult();
        result.setFilePath(to);

        try {
            ReadableByteChannel readableByteChannel = openChannelToResource(from);
            fileService.writeToFile(to, readableByteChannel);
            log.info("Saved information from {} to {}", from, to);



            result.setSuccessful(true);
        } catch (IOException e) {
            log.error("Exception occurred during processing.", e);
            log.info("Will try to delete file.");
            fileService.deleteFile(to);
        }
        return result;
    }

    protected abstract ReadableByteChannel openChannelToResource(URL recourse) throws IOException;

}
