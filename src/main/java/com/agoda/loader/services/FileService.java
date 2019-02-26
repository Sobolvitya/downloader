package com.agoda.loader.services;

import com.agoda.loader.exception.WrongResourcesException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileService {

    public boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }

    public boolean deleteFile(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error(String.format("Unable to delete file - %s", path), e);
        }
        return Files.exists(path);
    }

    public void writeToFile(Path filePath, ReadableByteChannel readableByteChannel) throws IOException {
        FileChannel fileChannel = new FileOutputStream(filePath.toFile()).getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
    }

    public boolean directoryExists(Path folderPath) {
        return Files.exists(folderPath) && Files.isDirectory(folderPath);
    }

    public boolean createDirectory(Path folderPath) {
        try {
            Files.createDirectory(folderPath);
        } catch (IOException e) {
            log.error(String.format("Unable to create directory - %s", folderPath), e);
        }
        return directoryExists(folderPath);
    }

    public boolean exists(Path filePath) {
        return Files.exists(filePath);
    }

    public boolean isWritable(Path filePath) {
        return Files.isWritable(filePath);
    }

    public void maybeCreateDirectory(String folderPath) {
        maybeCreateDirectory(Paths.get(folderPath));
    }

    public void maybeCreateDirectory(Path folderPath) {
        if (!directoryExists(folderPath)) {
            log.info("Creating directory - {}", folderPath);
            boolean directoryCreated = createDirectory(folderPath);
            if (!directoryCreated) {
                throw new WrongResourcesException("Unable to create directory:" + folderPath);
            }
        }
    }
}
