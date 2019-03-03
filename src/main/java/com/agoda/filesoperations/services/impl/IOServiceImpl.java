package com.agoda.filesoperations.services.impl;

import com.agoda.filesoperations.services.IOService;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class IOServiceImpl implements IOService {

    private static IOServiceImpl instance;

    private IOServiceImpl() {
    }

    public static IOServiceImpl getIOService() {
        if (instance == null) {
            synchronized (IOServiceImpl.class) {
                if (instance == null) {
                    instance = new IOServiceImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean deleteFile(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error(String.format("Unable to delete file - %s", path), e);
        }
        return Files.exists(path);
    }

    @Override
    public void writeToFile(Path filePath, ReadableByteChannel readableByteChannel) throws IOException {
        FileChannel fileChannel = new FileOutputStream(filePath.toFile()).getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        fileChannel.close();
    }

    @Override
    public boolean directoryExists(Path folderPath) {
        return Files.exists(folderPath) && Files.isDirectory(folderPath);
    }

    @Override
    public ReadableByteChannel openReadableChannel(URL url) throws IOException {
        return Channels.newChannel(url.openStream());
    }

    @Override
    public boolean createDirectory(Path folderPath) {
        try {
            Files.createDirectory(folderPath);
        } catch (IOException e) {
            log.error(String.format("Unable to create directory - %s", folderPath), e);
        }
        return directoryExists(folderPath);
    }

    @Override
    public boolean exists(Path filePath) {
        return Files.exists(filePath);
    }

    @Override
    public boolean isWritable(Path filePath) {
        return Files.isWritable(filePath);
    }

    @Override
    public boolean maybeCreateDirectory(Path folderPath) {
        if (!directoryExists(folderPath)) {
            log.info("Creating directory - {}", folderPath);
            return createDirectory(folderPath);
        }
        return true;
    }
}
