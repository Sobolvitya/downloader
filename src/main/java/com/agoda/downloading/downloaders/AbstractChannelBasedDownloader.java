package com.agoda.downloading.downloaders;

import com.agoda.downloading.domain.DownloadingResult;
import com.agoda.filesoperations.services.IOService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

@Slf4j
abstract class AbstractChannelBasedDownloader implements Downloader {

    protected final IOService ioService;

    protected AbstractChannelBasedDownloader(IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public DownloadingResult download(URL from, Path to) {

        DownloadingResult result = new DownloadingResult();
        result.setFilePath(to);

        try {
            ReadableByteChannel readableByteChannel = openChannelToResource(from);
            ioService.writeToFile(to, readableByteChannel);
            log.info("Saved information from {} to {}", from, to);
            result.setSuccessful(true);
        } catch (Throwable e) {
            log.error("Exception occurred during processing.", e);
            log.info("Will try to delete file.");
            ioService.deleteFile(to);
        }
        return result;
    }

    protected abstract ReadableByteChannel openChannelToResource(URL recourse) throws IOException;

}
