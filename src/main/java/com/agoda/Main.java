package com.agoda;

import com.agoda.loader.imp.HttpLoader;

public class Main {

    public static void main(String[] args) {
        HttpLoader httpLoader = new HttpLoader();
        httpLoader.load("http://www.mhhe.com/mayfieldpub/maner/resources/actb.txt", "/Users/viktor.sobol/Viktor/tech_task/test.txt");


    }
}
