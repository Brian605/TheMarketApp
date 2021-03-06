package com.returno.tradeit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.returno.tradeit.R;
import com.returno.tradeit.utils.Tagger;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagHolder> {
    private final Context context;
   private final List<String> tagList;

    public TagAdapter(Context context, List<String> tagList) {
        this.context = context;
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public TagHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new TagHolder(LayoutInflater.from(context).inflate(R.layout.tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TagHolder holder, int position) {
holder.textView.setText(tagList.get(position));
int id= new Tagger().getTagBackground();

while (id==Tagger.currentId){
   id=new Tagger().getTagBackground();
}

Tagger.currentId=id;
holder.textView.setBackground(context.getResources().getDrawable(id));

    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public static class TagHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        public TagHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.tagText);

        }
    }
}
