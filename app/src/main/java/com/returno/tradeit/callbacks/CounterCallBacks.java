package com.returno.tradeit.callbacks;

public interface CounterCallBacks {
    void counterResult(int count);
    void onError(String message);

    default void noData(){

    }
}
