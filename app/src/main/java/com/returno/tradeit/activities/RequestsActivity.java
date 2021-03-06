package com.returno.tradeit.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.returno.tradeit.R;
import com.returno.tradeit.adapters.ArrayIconAdapter;
import com.returno.tradeit.adapters.RequestsAdapter;
import com.returno.tradeit.callbacks.CompleteCallBacks;
import com.returno.tradeit.callbacks.DeleteCallBacks;
import com.returno.tradeit.callbacks.UploadCallBacks;
import com.returno.tradeit.models.Notification;
import com.returno.tradeit.models.Request;
import com.returno.tradeit.models.User;
import com.returno.tradeit.utils.Commons;
import com.returno.tradeit.utils.Constants;
import com.returno.tradeit.utils.FirebaseUtils;
import com.returno.tradeit.utils.ItemUtils;
import com.returno.tradeit.utils.UploadUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

@SuppressWarnings("unchecked")
public class RequestsActivity extends AppCompatActivity {


    private List<Request>requestList;
    private RequestsAdapter adapter;

    private ValueEventListener userListener;
    private String Poster_ID;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        RecyclerView recyclerView = findViewById(R.id.requestRecycler);
        Toolbar materialToolbar = findViewById(R.id.toolbar);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe);
         setSupportActionBar(materialToolbar);
        getSupportActionBar().setTitle("Item Requests");

        dialog=new Dialog(RequestsActivity.this);
        dialog.setContentView(R.layout.progressdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            if (!dialog.isShowing()) {
                dialog.show();
                fetchRequests();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 3000));
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setBackground(new ColorDrawable(Color.TRANSPARENT));

        requestList=new ArrayList<>();

        adapter=new RequestsAdapter(RequestsActivity.this, requestList, (view1, position) -> {
            TextView idView=view1.findViewById(R.id.requestId);
            TextView userIdView=view1.findViewById(R.id.userId);
            TextView phoneNumberView=view1.findViewById(R.id.userphone);

            String userId=userIdView.getText().toString();
            String requestId=idView.getText().toString();
            String phoneNumber=phoneNumberView.getText().toString();

            Timber.e(phoneNumber);
            MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(RequestsActivity.this,R.style.MaterialAlertDialog_MaterialComponents_Title_Text_CenterStacked);
            builder.setTitle("Choose Action");

            String[] actions;
            Integer[] icons;
            boolean isUser=new FirebaseUtils().isCurrentUser(userId);
            if (isUser){
               actions=new String[]{"Delete Item"};
               icons=new Integer[]{R.drawable.ic_delete};
            }else {
                actions=new String[]{"WhatsApp","Message","Call"};
                icons=new Integer[]{R.drawable.whatsapp,R.drawable.ic_message,R.drawable.phone_in_talk};
            }

            ListAdapter listAdapter= new  ArrayIconAdapter(RequestsActivity.this,actions,icons);
            builder.setAdapter(listAdapter, (dialogInterface, i) -> {
                Timber.e(String.valueOf(i));
                if (isUser && i == 0) {
                    Timber.e(String.valueOf(isUser));
                    deleteRequest(requestId);
                    return;
                }
                if (i == 0) {
                    whatsAppUser(phoneNumber);
                } else if (i == 1) {
                    messageUser(phoneNumber);
                } else if (i == 2) {
                    callUser(phoneNumber);
                }
            });
Dialog dialog=builder.create();
dialog.show();

        });


        recyclerView.setAdapter(adapter);
        fetchRequests();

    }

    private void callUser(String phoneNumber) {
        Commons.getInstance().callUser(RequestsActivity.this,phoneNumber);
    }

    private void messageUser(String phoneNumber) {
Commons.getInstance().sendMessage("hello",phoneNumber,RequestsActivity.this);
    }

    private void whatsAppUser(String phoneNumber) {
        try {
            Commons.getInstance().openWhatsApp(RequestsActivity.this,phoneNumber);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void deleteRequest(String requestId) {
        dialog.show();
        new UploadUtils().deleteRequest(requestId, new DeleteCallBacks() {
            @Override
            public void onDelete() {
                runOnUiThread(() -> {
                    if (dialog.isShowing())dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_LONG).show();
                    recreate();
                });
            }

            @Override
            public void onError(String message) {
runOnUiThread(() -> {
    if (dialog.isShowing())dialog.dismiss();
    new ItemUtils().showMessageDialog(RequestsActivity.this, message);
});
            }
        });

    }

    private void postRequest(String request) {
        dialog.show();
      getUser(objects -> {
          User user=(User)objects[0];
          String itemId=ItemUtils.generateItemId();
          Request request1=Request.getInstance()
                  .withRequestId(itemId)
                  .withUserId(user.getUserId())
                  .withRequestItem(request)
                  .withUserName(user.getUserName())
                  .withUserPhone(user.getPhoneNumber())
                  .build();
          new UploadUtils().postRequest(request1, new UploadCallBacks() {
              @Override
              public void onUploadSuccess() {
                  runOnUiThread(() -> {
                      if (dialog.isShowing()) dialog.dismiss();
                      Toast.makeText(getApplicationContext(), "Posted", Toast.LENGTH_LONG).show();
                      Notification notification = new Notification(itemId, "0", "requests", request, null);
                      new FirebaseUtils().postAPushNotification(notification);
                      recreate();
                  });

              }

              @Override
              public void onProgressUpdated(int newValue) {

              }

              @Override
              public void onError(String message) {
                  runOnUiThread(() -> {
                      if (dialog.isShowing()) dialog.dismiss();
                      new ItemUtils().showMessageDialog(RequestsActivity.this, message);
                  });
              }
          });
      });
    }

    private void fetchRequests() {
        new UploadUtils().fetchRequests(new CompleteCallBacks() {
            @Override
            public void onComplete(Object... objects) {
                runOnUiThread(() -> {
                   requestList.clear();
                   List<Request> requests= (List<Request>) objects[0];
                   for (Request request:requests){
                       Timber.e(request.getRequestItem());
                   }
                   requestList.addAll(requests);
                   adapter.notifyDataSetChanged();
                   if (dialog.isShowing())dialog.dismiss();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
               if (dialog.isShowing())dialog.dismiss();
               new ItemUtils().showMessageDialog(RequestsActivity.this, error);
                });
            }
        });
    }
  private void getUser(CompleteCallBacks listener){

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS_DIR).child(new FirebaseUtils().getCurrentUserId());
        userListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String phone=snapshot.child(Constants.USER_PHONE).getValue().toString();
                String username=snapshot.child(Constants.USER_NAME).getValue().toString();
                reference.removeEventListener(userListener);
                User user=new User(snapshot.getKey(),username,phone,null,0);
                listener.onComplete(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addListenerForSingleValueEvent(userListener);

    }
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.requests_menu,menu);

        MenuItem item=menu.findItem(R.id.search_bar);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setBackground(getResources().getDrawable(R.drawable.searchback));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu.getClass().getSimpleName().equals("MenuBuilder")){
            try {
                Method method=menu.getClass().getDeclaredMethod("setOptionalIconsVisible",Boolean.TYPE);
                method.invoke(menu,true);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.addRequest){
            showRequestDialog();
        }
        if (id==R.id.myRequests){
            adapter.getFilter().filter(new FirebaseUtils().getCurrentUserId());
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRequestDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(RequestsActivity.this);
        LayoutInflater inflater=LayoutInflater.from(RequestsActivity.this) ;
        View view=inflater.inflate(R.layout.input_dialog,null,false);

        builder.setView(view);
        Dialog dialog=builder.create();

        TextInputEditText requestEdit=view.findViewById(R.id.inputText);

        Button sendButton=view.findViewById(R.id.send);
        sendButton.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(requestEdit.getText().toString())){
                String request=requestEdit.getText().toString();
                if (dialog.isShowing())dialog.dismiss();
                postRequest(request);
            }
        });

        Button cancel=view.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> dialog.cancel());

        dialog.show();
    }

    @Override
    protected void onPause() {
        if (dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(RequestsActivity.this,LoginActivity.class));
        }
    }
}