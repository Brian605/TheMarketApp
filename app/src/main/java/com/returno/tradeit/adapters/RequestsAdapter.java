package com.returno.tradeit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.returno.tradeit.R;
import com.returno.tradeit.callbacks.RecyclerCallBacks;
import com.returno.tradeit.models.Request;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

@SuppressWarnings("unchecked")
public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> implements Filterable {
private final Context context;
private List<Request> requestList;
    private final List<Request> filteredList;
private final RecyclerCallBacks listener;

    public RequestsAdapter(Context context, List<Request> requestList, RecyclerCallBacks listener) {
        this.context = context;
        this.requestList = requestList;
        this.listener = listener;
        this.filteredList=requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.request_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        view.setOnClickListener(v -> listener.onItemClick(view,viewHolder.getAdapterPosition()));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
Request request=requestList.get(position);
String requestItem=request.getRequestItem().replace("*","").replace("\\","");
holder.requestItemView.setText(requestItem);
holder.requestIdView.setText(request.getRequestId());
holder.userphoneView.setText(request.getUserPhone());
holder.userIdView.setText(request.getUserId());
holder.usernameView.setText(request.getUserName());
        Timber.e(request.getRequestId());
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView usernameView;
        final TextView userphoneView;
        final TextView userIdView;
        final TextView requestIdView;
        final TextView requestItemView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdView=itemView.findViewById(R.id.userId);
            usernameView=itemView.findViewById(R.id.username);
            userphoneView=itemView.findViewById(R.id.userphone);
            requestIdView=itemView.findViewById(R.id.requestId);
            requestItemView=itemView.findViewById(R.id.requestItem);


        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults=new FilterResults();
                ArrayList<Request> items=new ArrayList<>();
                if (constraint==null ||constraint.length()==0 ){
                    filterResults.count=filteredList.size();
                    filterResults.values=filteredList;
                }else {
                    for (Request request:filteredList){
                        if (request.getRequestItem().toLowerCase().contains(constraint.toString().toLowerCase()) || request.getUserId().toLowerCase().equals(constraint.toString().toLowerCase())){
                            items.add(request);
                        }
                    }
                    filterResults.count=items.size();
                    filterResults.values=items;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                requestList = (ArrayList<Request>) results.values;
                notifyDataSetChanged();
                if (requestList.size()==0){
                    Toast.makeText(context,"No Results match your search",Toast.LENGTH_LONG).show();
                }
            }
        };
    }

}
