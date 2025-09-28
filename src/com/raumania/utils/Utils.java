package com.raumania.utils;

import java.io.InputStream;

public class Utils {
    public static InputStream getImage(String imgName) {
        ClassLoader classLoader = Utils.class.getClassLoader();
        return classLoader.getResourceAsStream(imgName);
    }
}
