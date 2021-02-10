package com.returno.tradeit.callbacks;

import timber.log.Timber;

public interface UploadCallBacks {
    void onUploadSuccess();
    void onProgressUpdated(int newValue);
    default void onError(String message){
        Timber.e(message);
    }
}
