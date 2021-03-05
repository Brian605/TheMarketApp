package com.returno.tradeit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.returno.tradeit.R;
import com.returno.tradeit.callbacks.RecyclerCallBacks;
import com.returno.tradeit.models.CategoryItem;

import java.util.List;

public class CategoriesRecyclerAdapter extends RecyclerView.Adapter<CategoriesRecyclerAdapter.ViewHolder> {

private Context context;
private List<CategoryItem> list;
private RecyclerCallBacks listener;

    public CategoriesRecyclerAdapter(Context context, List<CategoryItem> list, RecyclerCallBacks listener){
    this.context=context;
    this.list=list;
    this.listener=listener;
}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_recycler_item,parent,false);
        final ViewHolder viewHolder= new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view,viewHolder.getAdapterPosition());
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
CategoryItem categoryItem =list.get(position);
holder.categoryName.setText(categoryItem.getPost_title());
holder.categoryId.setText(categoryItem.getDesc());
Glide.with(context).load(categoryItem.getPost_url()).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

TextView categoryName,categoryId;
ImageView imageView;

ViewHolder(View itemview){
    super(itemview);

    categoryName=itemview.findViewById(R.id.categ);
    categoryId=itemview.findViewById(R.id.desc);
    imageView=itemview.findViewById(R.id.categImg);
}


    }

}
