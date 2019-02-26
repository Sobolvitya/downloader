package com.agoda.loader.imp;

import com.agoda.loader.services.FileService;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

class DefaultFileWriter extends AbstractLoaderToFile {

    public DefaultFileWriter() {
        super(new FileService());
    }

    @Override
    protected ReadableByteChannel openChannelToResource(URL recourse) throws IOException {
        return Channels.newChannel(recourse.openStream());
    }
}
