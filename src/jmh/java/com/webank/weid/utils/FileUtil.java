package com.webank.weid.utils;

import java.io.FileNotFoundException;

import org.springframework.util.ResourceUtils;

public class FileUtil {
    public static String getProjectPath() {
        String path = "";
        try {
            path= ResourceUtils.getURL("").getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return path;
    }

}
