package com.agoda.downloading.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DownloadingResult {

    Path filePath;

    boolean successful;
}
