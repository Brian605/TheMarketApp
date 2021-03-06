package com.returno.tradeit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.returno.tradeit.R;

public class SafetyAdapter extends BaseAdapter {
    final String[] items;
    final Context context;

    public SafetyAdapter(String[] items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.safety_list_item, parent, false);
            TextView textView = convertView.findViewById(R.id.text);
            textView.setText(items[position]);
        }else {
            TextView textView = convertView.findViewById(R.id.text);
            textView.setText(items[position]);
        }
        return convertView;
    }
}
