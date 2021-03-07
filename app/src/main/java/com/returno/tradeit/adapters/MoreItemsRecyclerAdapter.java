package com.returno.tradeit.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.returno.tradeit.R;
import com.returno.tradeit.callbacks.RecyclerCallBacks;
import com.returno.tradeit.models.Item;
import com.returno.tradeit.utils.ItemUtils;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

@SuppressWarnings("unchecked")
public class MoreItemsRecyclerAdapter extends RecyclerView.Adapter<MoreItemsRecyclerAdapter.ViewHolder>  implements Filterable {

private Context context;
private ArrayList<Item> list,filterList;
private RecyclerCallBacks listener;

    public MoreItemsRecyclerAdapter(){ }

public MoreItemsRecyclerAdapter(boolean isFav, Context context, ArrayList<Item> list, RecyclerCallBacks listener){
    this.context=context;
    this.list=list;
    this.listener=listener;
    this.filterList=list;
}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_recycler,parent,false);
        final ViewHolder viewHolder= new ViewHolder(view);
        view.setOnClickListener(view1 -> listener.onItemClick(view1,viewHolder.getAdapterPosition()));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
Item item=list.get(position);
holder.itemId.setText(item.getItemId());
holder.itemName.setText(item.getItemName());
holder.itemDescription.setText(item.getItemDescription());
holder.itemPrice.setText(formatCurrency(item.getItemPrice()));
holder.itemPosterId.setText(item.getItemPosterId());
holder.imageurl.setText(item.getItemImage());
holder.tagsView.setText(item.getItemTag());
holder.imageView.setImageURI(Uri.fromFile(new File(item.getItemImage().split("___")[0])));
holder.categoryView.setText(item.getItemCategory());

if (ItemUtils.isItemInFavorites(item.getItemId())){
    holder.favView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_favorite));
}

holder.favView.setOnClickListener(v -> {
    if (ItemUtils.isItemInFavorites(item.getItemId())){
        ItemUtils.removeFromFavorites(item.getItemId());
        holder.favView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_favorite_border));
        Toast.makeText(context,"Removed From Wish list",Toast.LENGTH_LONG).show();
       notifyItemChanged(position);
        return;

    }
    ItemUtils.addToFavorites(item.getItemId());
    notifyItemChanged(position);
    Toast.makeText(context,"Added to Wish list",Toast.LENGTH_LONG).show();
});
        Timber.e(item.getItemImage());
    }

    @Override
    public int getItemCount() {
      return list.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults=new FilterResults();
                ArrayList<Item> items=new ArrayList<>();
                if (constraint==null ||constraint.length()==0 ){
                    filterResults.count=filterList.size();
                    filterResults.values=filterList;
                }else {
                    for (Item item:filterList){
                        if (item.getItemName().toLowerCase().contains(constraint.toString().toLowerCase())
                        || item.getItemDescription().toLowerCase().contains(constraint.toString().toLowerCase())
                        || item.getItemTag().toLowerCase().contains(constraint.toString().toLowerCase())){
                            items.add(item);
                        }
                    }
                    filterResults.count=items.size();
                    filterResults.values=items;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                    list = (ArrayList<Item>) results.values;
 notifyDataSetChanged();
 if (list.size()==0){
     Toast.makeText(context,"No Results match your search",Toast.LENGTH_LONG).show();
 }
            }
        };
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

final TextView itemName;
        final TextView itemDescription;
        final TextView itemPrice;
        final TextView imageurl;
        final TextView itemPosterId;
        TextView itemDbId;
        final TextView itemId;
        final TextView tagsView;
        final TextView categoryView;
final ImageView favView;
final CircleImageView imageView;

ViewHolder(View itemView){
    super(itemView);

    itemName=itemView.findViewById(R.id.itemTitle);
    itemDescription=itemView.findViewById(R.id.itemDescription);
    itemPrice=itemView.findViewById(R.id.itemPrice);
    imageurl=itemView.findViewById(R.id.itemImageUrl);
    imageView=itemView.findViewById(R.id.itemImage);
    itemPosterId=itemView.findViewById(R.id.postUserId);
    itemId=itemView.findViewById(R.id.itemId);
    favView=itemView.findViewById(R.id.favorite);
    tagsView=itemView.findViewById(R.id.tagsView);
    categoryView=itemView.findViewById(R.id.itemCategory);



}


    }

    private String formatCurrency(int currency){
        NumberFormat format=NumberFormat.getInstance();
        format.setGroupingUsed(true);
       // format.setCurrency();
        return format.format(currency);
    }

}
