package com.returno.tradeit.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.returno.tradeit.R;
import com.returno.tradeit.adapters.ItemRecyclerAdapter;
import com.returno.tradeit.callbacks.FetchCallBacks;
import com.returno.tradeit.models.Item;
import com.returno.tradeit.utils.Constants;
import com.returno.tradeit.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FavoritesFragment extends Fragment {



   private RecyclerView recyclerView;
   ItemRecyclerAdapter adapter;
   List<Item>itemList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView=view.findViewById(R.id.recycler);
        TextView textView=view.findViewById(R.id.text);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

       new ItemUtils().fetchFavoritesFromDb(getActivity(), new FetchCallBacks() {
           @Override
           public void fetchComplete(List<Item> itemList) {
               adapter=new ItemRecyclerAdapter(true,getActivity(), (ArrayList<Item>) itemList, (view1, position) -> {

                   String[] options=new String[]{"Share","View More"};
                   AlertDialog.Builder builder=new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                   builder.setItems(options, (dialog, which) -> {
                       if (which==0){
                           TextView categView=view1.findViewById(R.id.itemCategory);
                           ItemUtils.share(view1,getActivity(),categView.getText().toString());
                           dialog.dismiss();
                       }else
                       if (which==1){
                          if (dialog!=null)dialog.dismiss();
                           TextView categView=view1.findViewById(R.id.itemCategory);
                           ItemUtils.goToSingleView(view1,getActivity(),categView.getText().toString(), Constants.MODE_LOCAL);
                           dialog.dismiss();
                       }
                   });
                   Dialog dialog=builder.create();
                   dialog.show();
               });

               recyclerView.setAdapter(adapter);
           }

           @Override
           public void fetchError(String message) {
               textView.setVisibility(View.VISIBLE);
               return;
           }
       });


        return view;
    }

}