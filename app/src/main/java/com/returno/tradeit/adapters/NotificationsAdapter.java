package com.returno.tradeit.adapters;

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
        view.setOnClickListener(view1 -> {

        });
        return viewHolder;
    }


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

final TextView messageView;
       final TextView dateView;
final LinearLayout linearLayout;


ViewHolder(View itemView){
    super(itemView);
    linearLayout=itemView.findViewById(R.id.root);
    messageView=itemView.findViewById(R.id.message);
    dateView=itemView.findViewById(R.id.date);

}


    }

}
