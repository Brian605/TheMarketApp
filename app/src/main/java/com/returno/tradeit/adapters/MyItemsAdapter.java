package com.returno.tradeit.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.returno.tradeit.R;
import com.returno.tradeit.callbacks.RecyclerCallBacks;
import com.returno.tradeit.models.Item;
import com.returno.tradeit.utils.ItemUtils;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class MyItemsAdapter extends RecyclerView.Adapter<MyItemsAdapter.ViewHolder> {

private Context context;
private List<Item> list;
private RecyclerCallBacks listener;

public MyItemsAdapter(){ }

public MyItemsAdapter(Context context, List<Item> list, RecyclerCallBacks listener){
    this.context=context;
    this.list=list;
    this.listener=listener;
    Timber.e(String.valueOf(list.size()));
}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recycler_item,parent,false);
        final ViewHolder viewHolder= new ViewHolder(view);
        view.setOnClickListener(view1 -> listener.onItemClick(view1,viewHolder.getAdapterPosition()));
        return viewHolder;
    }

    @SuppressLint("LogNotTimber")
    @Override
    public void onBindViewHolder(@NonNull MyItemsAdapter.ViewHolder holder, int position) {
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

        holder.favView.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
      return list.size();
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
            counterView=itemView.findViewById(R.id.counterText);
            categoryView=itemView.findViewById(R.id.itemCategory);

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
