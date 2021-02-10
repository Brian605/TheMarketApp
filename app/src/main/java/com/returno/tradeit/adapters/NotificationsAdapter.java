package com.returno.tradeit.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.returno.tradeit.R;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

private List<String> list;

public NotificationsAdapter(){ }

public NotificationsAdapter( List<String> list){
    this.list=list;

}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item,parent,false);
        final ViewHolder viewHolder=new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // listener.itemclick(view,viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @SuppressLint("LogNotTimber")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
holder.messageView.setText(list.get(position));
holder.dateView.setVisibility(View.GONE);
ViewGroup.MarginLayoutParams params=(ViewGroup.MarginLayoutParams)holder.linearLayout.getLayoutParams();
params.leftMargin=10;

    }

    @Override
    public int getItemCount() {
      return list.size();
    }

   static class ViewHolder extends RecyclerView.ViewHolder{

TextView messageView,dateView;
LinearLayout linearLayout;


ViewHolder(View itemview){
    super(itemview);
    linearLayout=itemview.findViewById(R.id.root);
    messageView=itemview.findViewById(R.id.message);
    dateView=itemview.findViewById(R.id.date);

}


    }

}
