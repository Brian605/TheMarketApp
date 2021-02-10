package com.returno.tradeit.callbacks;

import android.view.View;

public interface RecyclerCallBacks {

void onItemClick(View view, int position);

default void onLongClick(View view,int position){

}
}
