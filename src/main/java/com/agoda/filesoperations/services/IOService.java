package com.agoda.filesoperations.services;

import java.io.IOException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

public interface IOService {

    boolean deleteFile(Path path);

    void writeToFile(Path filePath, ReadableByteChannel readableByteChannel) throws IOException;

    boolean createDirectory(Path folderPath);

    boolean exists(Path filePath);

    boolean isWritable(Path filePath);

    boolean maybeCreateDirectory(Path folderPath);

    boolean directoryExists(Path folderPath);

    ReadableByteChannel openReadableChannel(URL url) throws IOException;
}
