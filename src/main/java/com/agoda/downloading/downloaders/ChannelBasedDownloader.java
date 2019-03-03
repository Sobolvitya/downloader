package com.agoda.downloading.downloaders;

import com.agoda.filesoperations.services.IOService;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;

class ChannelBasedDownloader extends AbstractChannelBasedDownloader {

    ChannelBasedDownloader(IOService ioService) {
        super(ioService);
    }

    @Override
    protected ReadableByteChannel openChannelToResource(URL recourse) throws IOException {
        return this.ioService.openReadableChannel(recourse);
    }
}
