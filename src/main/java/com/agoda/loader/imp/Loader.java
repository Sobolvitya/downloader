package com.agoda.loader.imp;

import com.agoda.loader.DownloadingResult;

import java.net.URL;
import java.nio.file.Path;

public interface Loader {

    DownloadingResult load(URL from, Path to);
}
