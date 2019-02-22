package com.agoda.loader.imp;

import com.agoda.loader.Loader;
import com.agoda.loader.LoaderType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.agoda.loader.LoaderType.HTTP;

public class HttpLoader implements Loader {

    @Override
    public void load(String recourse, String location) {
        try {
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(recourse).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(location);
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            try {
                Files.delete(Paths.get(location));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        finally {

        }

    }

    @Override
    public LoaderType type() {
        return HTTP;
    }
}
