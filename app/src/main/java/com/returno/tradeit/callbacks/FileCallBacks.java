package com.returno.tradeit.callbacks;

import java.io.File;

public interface FileCallBacks {
    default void onDirectoryCreated(String dir){

    }
    default void onFileCreated(File file){

    }
}
