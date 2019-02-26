package com.agoda.loader.imp;

import com.agoda.loader.LoaderType;

import java.util.HashMap;
import java.util.Map;

import static com.agoda.loader.LoaderType.*;

public class LoaderFactory {

    private static Map<LoaderType, Loader> loaders = new HashMap<>();

    static {
        DefaultFileWriter defaultFileWriter = new DefaultFileWriter();
        loaders.put(HTTP, defaultFileWriter);
        loaders.put(FTP, defaultFileWriter);
        loaders.put(SFTP, defaultFileWriter);
    }

    public static Loader getLoader(LoaderType loaderType) {
        return loaders.get(loaderType);
    }
}
