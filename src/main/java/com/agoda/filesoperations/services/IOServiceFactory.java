package com.agoda.filesoperations.services;

import com.agoda.filesoperations.services.impl.IOServiceImpl;

public class IOServiceFactory {

    public static IOService getIOService() {
        return IOServiceImpl.getIOService();
    }
}
