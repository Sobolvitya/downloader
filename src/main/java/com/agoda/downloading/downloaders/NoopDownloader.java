package com.agoda.downloading.downloaders;

import com.agoda.downloading.domain.DownloadingResult;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.nio.file.Path;

@Slf4j
public class NoopDownloader implements Downloader {

    @Override
    public DownloadingResult download(URL from, Path to) {
        log.warn("Unsupported url - {}. Skipping downloading", from);
        return DownloadingResult.builder()
                .filePath(to)
                .successful(false)
                .build();
    }
}
