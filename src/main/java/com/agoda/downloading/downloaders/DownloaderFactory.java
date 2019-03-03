package com.agoda.downloading.downloaders;

public interface DownloaderFactory {

    Downloader getDownloader(String protocol);
}
