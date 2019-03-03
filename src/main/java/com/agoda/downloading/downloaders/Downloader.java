package com.agoda.downloading.downloaders;

import com.agoda.downloading.domain.DownloadingResult;

import java.net.URL;
import java.nio.file.Path;

public interface Downloader {

    DownloadingResult download(URL from, Path to);
}
