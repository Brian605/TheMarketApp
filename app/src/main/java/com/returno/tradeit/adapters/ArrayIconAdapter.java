package com.returno.tradeit.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class ArrayIconAdapter extends ArrayAdapter<String> {
    final List<Integer>icons;
    public ArrayIconAdapter(Context context, List<String> items,List<Integer> icons){
        super(context, android.R.layout.select_dialog_item,items);
        this.icons=icons;
    }
    public ArrayIconAdapter(Context context,String[]items,Integer[]icons){
        super(context, android.R.layout.select_dialog_item,items);
        this.icons= Arrays.asList(icons);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=super.getView(position,convertView,parent);
        TextView textView=view.findViewById(android.R.id.text1);

        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(icons.get(position),0,0,0);
        textView.setCompoundDrawablePadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,12,getContext().getResources().getDisplayMetrics()));
        return view;
    }
}
