package com.returno.tradeit.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.returno.tradeit.R;
import com.returno.tradeit.activities.LoginActivity;
import com.returno.tradeit.activities.UserActivity;
import com.returno.tradeit.adapters.ItemRecyclerAdapter;
import com.returno.tradeit.adapters.MyItemsAdapter;
import com.returno.tradeit.callbacks.FetchCallBacks;
import com.returno.tradeit.models.Item;
import com.returno.tradeit.utils.Constants;
import com.returno.tradeit.utils.FirebaseUtils;
import com.returno.tradeit.utils.ItemUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMe extends Fragment {
RecyclerView recyclerView;
private ItemRecyclerAdapter adapter;
private List<Item> itemList;
private ValueEventListener listener;
private DatabaseReference reference;
private String currentUserId;
    private MyItemsAdapter myItemsAdapter;

    public FragmentMe() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_me, container, false);

        recyclerView=view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.setHasFixedSize(true);

        itemList=new ArrayList<>();
        myItemsAdapter =new MyItemsAdapter(getActivity(), itemList, (view1, position) -> {
            // throw new UnsupportedOperationException("Operation Not Yet Supported");
            // Log.wtf("1","2");
            String[] items = new String[]{"Share", "View more"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        dialog.dismiss();
                        TextView categView=view1.findViewById(R.id.itemCategory);
                        ItemUtils.share(view1,getActivity(),categView.getText().toString());
                    } else if (which == 1) {
                        TextView categView=view1.findViewById(R.id.itemCategory);
                        ItemUtils.goToSingleView(view1,getActivity(),categView.getText().toString(),Constants.MODE_LOCAL);
                        dialog.dismiss();
                    }
                }
            });
            Dialog dialog = builder.create();
            dialog.show();

        });
        recyclerView.setAdapter(myItemsAdapter);
fetchMyData();
        return view;
    }

    public void fetchMyData(){
        currentUserId=new  FirebaseUtils().getCurrentUserId();
     new ItemUtils().fetchLocalItemsById(getActivity(), currentUserId, new FetchCallBacks() {
         @Override
         public void fetchComplete(List<Item> itemsList) {
             itemList.clear();
             itemList.addAll(itemsList);
             getActivity().runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     myItemsAdapter.notifyDataSetChanged();
                 }
             });

         }

         @Override
         public void fetchError(String message) {

         }
     });

    }


    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        itemList.clear();
        fetchMyData();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.profile){
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS_DIR).child(currentUserId);
            listener=new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                  String username=snapshot.child(Constants.USER_NAME).getValue().toString();
                  String userPhone=snapshot.child(Constants.USER_PHONE).getValue().toString();
                  reference.removeEventListener(listener);
                    Intent intent=new Intent(getActivity(), UserActivity.class);
                    intent.putExtra(Constants.USER_NAME,username);
                    intent.putExtra(Constants.USER_ID,currentUserId);
                    intent.putExtra(Constants.USER_PHONE,userPhone);
                    startActivity(intent);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
listener.onCancelled(error);
                }
            };
            reference.addListenerForSingleValueEvent(listener);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.getClass().getSimpleName().equals("MenuBuilder")){
            try {

                Method method=menu.getClass().getDeclaredMethod("setOptionalIconsVisible",Boolean.TYPE);
                method.setAccessible(true);
                method.invoke(menu,true);
            }catch (NoSuchMethodException |IllegalAccessException | InvocationTargetException e){
                e.printStackTrace();
            }
        }
    }
}
