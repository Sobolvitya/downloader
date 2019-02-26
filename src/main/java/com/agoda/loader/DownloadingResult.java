package com.agoda.loader;

import lombok.Data;

import java.nio.file.Path;

@Data
public class DownloadingResult {
    Path filePath;

    boolean successful;
}
