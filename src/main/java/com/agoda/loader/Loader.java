package com.agoda.loader;

public interface Loader {

    void load(String recourse, String location);

    LoaderType type();

}
