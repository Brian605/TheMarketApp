package com.returno.tradeit.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.returno.tradeit.R;
import com.returno.tradeit.adapters.MoreItemsRecyclerAdapter;
import com.returno.tradeit.adapters.TagAdapter;
import com.returno.tradeit.callbacks.DeleteCallBacks;
import com.returno.tradeit.callbacks.FetchCallBacks;
import com.returno.tradeit.callbacks.RecyclerCallBacks;
import com.returno.tradeit.local.DatabaseManager;
import com.returno.tradeit.local.PreferenceManager;
import com.returno.tradeit.models.Item;
import com.returno.tradeit.utils.Commons;
import com.returno.tradeit.utils.Constants;
import com.returno.tradeit.utils.ItemUtils;
import com.returno.tradeit.utils.UploadUtils;
import com.returno.tradeit.utils.Urls;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import timber.log.Timber;

public class SingLeItemActivity extends AppCompatActivity {
    private String Username;
    private String UserPhone;
    private String posterId;
    private String Category;
    private String itemId;
    private String imageUrisVar;
    private String itemTags;

    private TextView posterNameView,counterView;

private Dialog dialog=null;
AlertDialog.Builder builder;

private int DbId;
static int status=0;
private BroadcastReceiver sentStatus,deleveredStatus;
private ValueEventListener listener,listener1;
private List<String>tagList;
private RecyclerView recyclerView,moreRecycler;
private MoreItemsRecyclerAdapter moreItemsRecyclerAdapter;
private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_le_item);
        posterNameView=findViewById(R.id.posterUserName);
        TextView itemTitleView = findViewById(R.id.singTitle);
        TextView itemDescriptionView = findViewById(R.id.singDesc);
        TextView itemPriceView = findViewById(R.id.singPrice);
        ImageView itemImageView = findViewById(R.id.singImage);
        counterView=findViewById(R.id.counterText);
        Toolbar toolbar = findViewById(R.id.toolbar);
        counterView.setVisibility(View.GONE);

        recyclerView=findViewById(R.id.recycler);
        recyclerView.setMinimumHeight(100);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        tagList=new ArrayList<>();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        Objects.requireNonNull(toolbar.getOverflowIcon()).setColorFilter(getResources().getColor(R.color.colorwhite), PorterDuff.Mode.SRC_ATOP);

        BottomNavigationView bottomNavigationView=findViewById(R.id.nav_view);

        Intent intent=getIntent();
        posterId = intent.getStringExtra(Constants.POSTER_ID);
        imageUrisVar= intent.getStringExtra(Constants.ITEM_IMAGE);
        String itemTitle = intent.getStringExtra(Constants.ITEM_TITLE);
        String itemDescription = intent.getStringExtra(Constants.ITEM_DESCRIPTION);
        String itemPrice = intent.getStringExtra(Constants.ITEM_PRICE);
        itemId=intent.getStringExtra(Constants.ITEM_ID);
        Category=intent.getStringExtra(Constants.ITEM_CATEGORY);
        itemTags=intent.getStringExtra(Constants.ITEM_TAG);
        String requestMode=intent.getStringExtra(Constants.MODE);

        setCounter();
if (requestMode!=null && requestMode.equals(Constants.MODE_LINK)){
List<String> images =ItemUtils.getExtraImagesUri(ItemUtils.getExtraImagesString(imageUrisVar));
String imageToLoad= Urls.BASE_URL+images.get(0);
    Glide.with(SingLeItemActivity.this).load(imageToLoad).into(itemImageView);
    Timber.e(imageUrisVar);
}else {
    File file = new File(ItemUtils.getLocalImageUri(imageUrisVar));

    if (file.exists()) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        Glide.with(SingLeItemActivity.this).load(bitmap).into(itemImageView);

    }
}
setItemTags();
showTutorial();
        itemTitleView.setText(itemTitle);
        itemDescriptionView.setText(itemDescription);
        itemPriceView.setText(getResources().getString(R.string.shilling_notation,itemPrice));
        String currentUser= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        //check if current user is the poster and hide delete button
        if (!currentUser.equals(posterId)){

            bottomNavigationView.findViewById(R.id.Delete).setVisibility(View.GONE);
            status=1;
        }
        if (currentUser.equals(posterId)){
            bottomNavigationView.findViewById(R.id.call).setVisibility(View.GONE);
            bottomNavigationView.findViewById(R.id.message).setVisibility(View.GONE);
            bottomNavigationView.findViewById(R.id.whatsapp).setVisibility(View.GONE);
        }

       final DatabaseReference reference= FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS_DIR).child(posterId);
       listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Username= Objects.requireNonNull(dataSnapshot.child(Constants.USER_NAME).getValue()).toString();
                    UserPhone= Objects.requireNonNull(dataSnapshot.child(Constants.USER_PHONE).getValue()).toString();
                    posterNameView.setText(getResources().getString(R.string.posted_by_notation,Username));
                    posterNameView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewUser(Username);
                        }
                    });


                }catch (Exception e){
                    e.printStackTrace();
                }



reference.removeEventListener(listener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

       reference.addListenerForSingleValueEvent(listener);

      // bottomNavigationView.set
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
          int i=menuItem.getItemId();
          if(i==R.id.call){
         Commons.getInstance().callUser(SingLeItemActivity.this,UserPhone) ;

          }else
              if(i==R.id.Delete){
                  deleteItem();
              }
          else
              if (i==R.id.message){
                  buyItem();
              }
           else
               if (i==R.id.whatsapp){
                   try {
                       Commons.getInstance().openWhatsApp(SingLeItemActivity.this,UserPhone);
                   } catch (UnsupportedEncodingException e) {
                       e.printStackTrace();
                   }
               }

                return false;
            }
        });
    }

    private void showTutorial() {
        if (!PreferenceManager.getInstance().isBoleanValueTrue(Constants.IS_SINGLE_FIRST_LAUNCH,this)){
            return;
        }
        ShowcaseView.Builder builder = new ShowcaseView.Builder(this);
        ViewTarget viewTarget = new ViewTarget(posterNameView);
        builder.setTarget(viewTarget)
                .setContentTitle("Tips")
                .setContentText("Click here to view this User's Profile")
                .withHoloShowcase()
                .setStyle(R.style.ShowCaseGreen)
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                        // showcaseView.hide();
                        PreferenceManager.getInstance().storeBooleanValue(Constants.IS_SINGLE_FIRST_LAUNCH,false,SingLeItemActivity.this);

                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        // callBacks.onComplete(null);

                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                    }
                })
                .build();
    }

    private void setCounter() {
        if (ItemUtils.hasMultiImage(imageUrisVar)){
            counterView.setVisibility(View.VISIBLE);
            int extra=ItemUtils.getExtraImagesUri(ItemUtils.getExtraImagesString(imageUrisVar)).size();
            counterView.setText(String.format(Locale.getDefault(),"+%d", extra));
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    RotateAnimation rotateAnimation=new RotateAnimation(0,180, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                    rotateAnimation.setDuration(1000);
                    rotateAnimation.setRepeatCount(Animation.INFINITE);
                    //rotateAnimation.setInterpolator(new LinearInterpolator());
                    counterView.startAnimation(rotateAnimation);
                }
            });
            thread.start();
            counterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemUtils.showMultipleImages(ItemUtils.getExtraImagesUri(ItemUtils.getExtraImagesString(imageUrisVar)),SingLeItemActivity.this);
                }
            });

        }
    }

    private void setItemTags() {
        tagList.addAll(ItemUtils.getTagsList(itemTags));
        recyclerView.setAdapter(new TagAdapter(SingLeItemActivity.this,tagList));
    }


    private void viewUser(String username) {

        Intent bundle=new Intent(SingLeItemActivity.this, UserActivity.class);
        bundle.putExtra(Constants.USER_ID,posterId);
        bundle.putExtra(Constants.USER_PHONE,UserPhone);
        bundle.putExtra(Constants.USER_NAME,username);
       startActivity(bundle);
    }

    private void buyItem(){
        LayoutInflater inflater=LayoutInflater.from(SingLeItemActivity.this) ;
        View view=inflater.inflate(R.layout.input_dialog,null,false);
        builder=new AlertDialog.Builder(SingLeItemActivity.this)  ;
        builder.setTitle("Contact Seller");
        builder.setView(view);
        dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);

        final TextInputEditText mess;
        Button buy,cancel;
        mess= view.findViewById(R.id.inputText)  ;
        buy=view.findViewById(R.id.send);
        cancel=view.findViewById(R.id.cancel);

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sms= Objects.requireNonNull(mess.getText()).toString();

                if (!TextUtils.isEmpty(sms)){
                    Commons.getInstance().sendMessage(sms,UserPhone,SingLeItemActivity.this);}

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void onResume(){
        super.onResume();
        sentStatus=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
         String s="An Error Occured" ;
         switch (getResultCode()){
             case Activity
                  .RESULT_OK  :s="Message Sent" ;
             break;
             case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                 s="Genereic failure";
                 break;
                 default:s="Sms Not Sent";
                 break;
         }
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show(); ;

            }
        };

        deleveredStatus=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s="An Error Occured" ;
                switch (getResultCode()){
                    case Activity
                            .RESULT_OK  :s="Message Sent" ;
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        s="Genereic failure";
                        break;
                    default:s="Sms Not Sent";
                        break;
                }
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }
        };

        registerReceiver(sentStatus,new IntentFilter(Constants.SENT_STATUS));
        registerReceiver(deleveredStatus,new IntentFilter(Constants.RECEIVED_STATUS));
    }

    public void onPause(){
        super.onPause();
        unregisterReceiver(sentStatus);
        unregisterReceiver(deleveredStatus);
        if (dialog!=null && dialog.isShowing() ){
            dialog.dismiss();
        }
    }

    private void deleteItem(){
        dialog=new Dialog(SingLeItemActivity.this);
        dialog.setContentView(R.layout.progressdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        DatabaseManager manager=new DatabaseManager(getApplicationContext()).open();
        manager.deleteItem(itemId);
        manager.close();

        File file=new File(ItemUtils.getLocalImageUri(imageUrisVar));
        try {
            boolean delete = file.delete();

        }catch (Exception e){
            if (dialog.isShowing())dialog.dismiss();
            new ItemUtils().showMessageDialog(SingLeItemActivity.this, e.getMessage());
        }

       String imagesUri=ItemUtils.getExtraImagesString(imageUrisVar);
        Timber.e(imagesUri);
        Thread thread=new Thread(
                () -> new UploadUtils().deleteItem(imagesUri, itemId, new DeleteCallBacks() {
                    @Override
                    public void onDelete() {
                        showDeleteSuccessNotification();
                        if (dialog.isShowing())dialog.dismiss();
                        new ItemUtils().showMessageDialog(SingLeItemActivity.this, "Item Deleted");
                        Intent intent=new Intent(SingLeItemActivity.this,CategoryViewActivity.class);
                        intent.putExtra(Constants.ITEM_CATEGORY,Category);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        if (dialog.isShowing())dialog.dismiss();
                        new ItemUtils().showMessageDialog(SingLeItemActivity.this, "Could not Delete");

                    }
                })
        );
        thread.start();

    }

    private void showDeleteSuccessNotification() {
        Bitmap icon= BitmapFactory.decodeResource(getResources(),R.drawable.logo1);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this, getResources().getString(R.string.app_name))
                .setAutoCancel(true)
                .setContentTitle("Background Tasks")
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.loginheader))
                .setLargeIcon(icon)
                .setContentText("The Item was Deleted Successfully");

        NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(new Random().nextInt(),builder.build());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_item_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.more){
            showMoreDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMoreDialog() {

        MaterialAlertDialogBuilder builder=new MaterialAlertDialogBuilder(SingLeItemActivity.this);
        View dialogView=LayoutInflater.from(SingLeItemActivity.this).inflate(R.layout.recyclerview_dialog,null,false);
        moreRecycler=dialogView.findViewById(R.id.recycler);
        SwipeRefreshLayout refreshLayout=dialogView.findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog.isShowing())dialog.dismiss();
                        showMoreDialog();
                        refreshLayout.setRefreshing(false);
                    }
                },3000);

            }
        });
        moreRecycler.setHasFixedSize(true);
        moreRecycler.setLayoutManager(new LinearLayoutManager(this));
        builder.setView(dialogView);
        dialog=builder.create();
        dialog.show();
        new ItemUtils().fetchLocalItemsById(SingLeItemActivity.this, posterId, new FetchCallBacks() {
            @Override
            public void fetchComplete(List<Item> list) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        moreRecycler.setAdapter(new MoreItemsRecyclerAdapter(false, SingLeItemActivity.this, (ArrayList<Item>) list, new RecyclerCallBacks() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if (dialog.isShowing())dialog.dismiss();
                               ItemUtils.goToSingleView(view,SingLeItemActivity.this,Category,Constants.MODE_LOCAL);
                            }
                        }));
                    }
                });
            }

            @Override
            public void fetchError(String message) {

            }
        });
    }

}
