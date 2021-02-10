package com.returno.tradeit.callbacks;

import com.returno.tradeit.models.Item;

import java.util.List;

public interface FetchCallBacks {
    void fetchComplete(List<Item>itemList);
    void fetchError(String message);
}
