package com.agoda.downloading.downloaders;

import com.agoda.filesoperations.services.IOServiceFactory;

import java.util.HashMap;
import java.util.Map;

public class DownloaderFactoryImpl implements DownloaderFactory {

    private static Map<String, Downloader> loaders = new HashMap<>();

    private static final Downloader NOOP_DOWNLOADER = new NoopDownloader();

    static {
        ChannelBasedDownloader downloader = new ChannelBasedDownloader(IOServiceFactory.getIOService());
        loaders.put("http", downloader);
        loaders.put("ftp", downloader);
        loaders.put("sftp", downloader);
    }

    @Override
    public Downloader getDownloader(String protocol) {
        return loaders.getOrDefault(protocol.toLowerCase(), NOOP_DOWNLOADER);
    }
}
