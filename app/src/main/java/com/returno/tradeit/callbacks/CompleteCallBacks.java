package com.returno.tradeit.callbacks;

public interface CompleteCallBacks {
    void onComplete(Object... objects);
    default void onFailure(String error){

    }
}
