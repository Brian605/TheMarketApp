package com.returno.tradeit.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.returno.tradeit.R;
import com.returno.tradeit.callbacks.RecyclerCallBacks;
import com.returno.tradeit.models.Item;
import com.returno.tradeit.utils.ItemUtils;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings("unchecked")
public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ViewHolder>  implements Filterable {

private Context context;
private ArrayList<Item> list,filterList;
private RecyclerCallBacks listener;

    public ItemRecyclerAdapter(){ }

public ItemRecyclerAdapter(boolean isFav, Context context, ArrayList<Item> list, RecyclerCallBacks listener){
    this.context=context;
    this.list=list;
    this.listener=listener;
    this.filterList=list;
}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recycler_item,parent,false);
        final ViewHolder viewHolder= new ViewHolder(view);
        view.setOnClickListener(view1 -> listener.onItemClick(view1,viewHolder.getAdapterPosition()));
        view.setOnLongClickListener(v -> {
            listener.onLongClick(v,viewHolder.getAdapterPosition());
            return true;
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.counterView.setVisibility(View.GONE);
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

        if (ItemUtils.hasMultiImage(item.getItemImage())){
            holder.counterView.setVisibility(View.VISIBLE);
            int extra=ItemUtils.getExtraImagesUri(ItemUtils.getExtraImagesString(item.getItemImage())).size();
            holder.counterView.setText(String.format(Locale.getDefault(),"+%d", extra));
            Thread thread=new Thread(() -> ((AppCompatActivity)context).runOnUiThread(() -> {
                RotateAnimation rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(1000);
                rotateAnimation.setRepeatCount(Animation.INFINITE);
                //rotateAnimation.setInterpolator(new LinearInterpolator());
                holder.counterView.startAnimation(rotateAnimation);
            }));
            thread.start();
holder.counterView.setOnClickListener(v -> ItemUtils.showMultipleImages(ItemUtils.getExtraImagesUri(ItemUtils.getExtraImagesString(item.getItemImage())),context));
        }

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
                        float price;
                        try {
                            price=Float.parseFloat(constraint.toString());
                        }catch (NumberFormatException e){
                            price=0;
                        }
                        if (item.getItemName().toLowerCase().contains(constraint.toString().toLowerCase())
                        || item.getItemDescription().toLowerCase().contains(constraint.toString().toLowerCase())
                        || item.getItemTag().toLowerCase().contains(constraint.toString().toLowerCase())
                        || (price!=0 && item.getItemPrice()<=price)){
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
        final TextView counterView;
        final TextView categoryView;
final ImageView favView;
final SimpleDraweeView imageView;

ViewHolder(View itemview){
    super(itemview);

    itemName=itemview.findViewById(R.id.itemTitle);
    itemDescription=itemview.findViewById(R.id.itemDescription);
    itemPrice=itemview.findViewById(R.id.itemPrice);
    imageurl=itemview.findViewById(R.id.itemImageUrl);
    imageView=itemview.findViewById(R.id.itemImage);
    itemPosterId=itemview.findViewById(R.id.postUserId);
    itemId=itemview.findViewById(R.id.itemId);
    favView=itemview.findViewById(R.id.favorite);
    tagsView=itemview.findViewById(R.id.tagsView);
    counterView=itemview.findViewById(R.id.counterText);
    categoryView=itemview.findViewById(R.id.itemCategory);

    imageView.setActivated(true);


}


    }

    private String formatCurrency(int currency){
        NumberFormat format=NumberFormat.getInstance();
        format.setGroupingUsed(true);
       // format.setCurrency();
        return format.format(currency);
    }

}
