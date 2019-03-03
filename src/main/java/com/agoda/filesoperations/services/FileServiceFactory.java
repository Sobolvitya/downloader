package com.agoda.filesoperations.services;

import com.agoda.filesoperations.services.impl.IOServiceImpl;

public class FileServiceFactory {

    public static IOService getFileService() {
        return IOServiceImpl.getIOService();
    }
}
