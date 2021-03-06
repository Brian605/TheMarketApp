package com.returno.tradeit.activities;


import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.returno.tradeit.R;
import com.returno.tradeit.adapters.NotificationsAdapter;
import com.returno.tradeit.callbacks.CompleteCallBacks;
import com.returno.tradeit.utils.Commons;
import com.returno.tradeit.utils.Constants;
import com.returno.tradeit.utils.FirebaseUtils;
import com.returno.tradeit.utils.ItemUtils;
import com.returno.tradeit.utils.Reducer;
import com.returno.tradeit.utils.Tagger;
import com.returno.tradeit.utils.UserUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

public class UserActivity extends AppCompatActivity {
    private String UserImage="none";
    private String UserLocation="Not Set Yet";
    private String UserId;
    private TextView locationText;
private AppCompatRatingBar ratingBar;
private CircleImageView userImageView;
    private ValueEventListener listener,listener1,imageListener,locationlistener,ratingListener,listener2,messageListener;
private static final int GALLERY_REQUEST=1;
private String downloadUrl;
    private Uri filePath;
private String cityName;
private int totalRating;
private float currentRating;
private Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user);

        Intent intent = getIntent();

        assert intent != null;
        String userName = intent.getStringExtra(Constants.USER_NAME);
        UserId = intent.getStringExtra(Constants.USER_ID);
        String userPhone = intent.getStringExtra(Constants.USER_PHONE);

        TextView nameText = findViewById(R.id.username);
        TextView phoneText = findViewById(R.id.phone);
        locationText = findViewById(R.id.location);
        ratingBar = findViewById(R.id.ratingbar);
        userImageView = findViewById(R.id.userImage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        CircleImageView messageView = findViewById(R.id.message);
        CircleImageView whatsappView = findViewById(R.id.whatsApp);
        CircleImageView callView = findViewById(R.id.call);

         nameText.setText(String.format("Am %s You can use any of the links below to contact me", userName));
        phoneText.setText(userPhone);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        ratingBar.setNumStars(5);
        Objects.requireNonNull(toolbar.getOverflowIcon()).setColorFilter(getResources().getColor(R.color.color_white), PorterDuff.Mode.SRC_ATOP);

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
if (fromUser){
totalRating+=(int)rating;

Toast.makeText(getApplicationContext(),"New Rating "+totalRating,Toast.LENGTH_LONG).show();
updateRating(totalRating);
}

        });

        messageView.setOnClickListener(view -> Commons.getInstance().sendMessage("Hello",userPhone,UserActivity.this));

        whatsappView.setOnClickListener(v -> {
            try {
                Commons.getInstance().openWhatsApp(UserActivity.this,userPhone);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

        callView.setOnClickListener(v -> Commons.getInstance().callUser(UserActivity.this,userPhone));
        dialog=new Dialog(UserActivity.this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
       dialog.setContentView(R.layout.progressdialog);
         fetchUserImage();

         userImageView.setOnClickListener(v -> {
             BitmapDrawable drawable=(BitmapDrawable)userImageView.getDrawable();
             Bitmap bitmap=drawable.getBitmap();

             View layout= LayoutInflater.from(UserActivity.this).inflate(R.layout.user_profile_image_zoom,null,false);
         AlertDialog.Builder builder=new AlertDialog.Builder(UserActivity.this);
         CircleImageView circleImageView=layout.findViewById(R.id.image);
         circleImageView.setImageBitmap(bitmap);
             RelativeLayout root=layout.findViewById(R.id.root);
             root.setBackground(new ColorDrawable(Color.TRANSPARENT));
         builder.setView(layout);
         if (dialog.isShowing())dialog.dismiss();
         Dialog dialog1=builder.create();
         if (dialog1.getWindow()!=null){
             dialog1.getWindow().getAttributes().windowAnimations=R.style.ZoomingDialogAnims;
             dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
         }
         dialog1.show();

         });

    }

    private void updateRating(int totalRating) {
        if (new FirebaseUtils().isCurrentUser(UserId)){
            Toast.makeText(getApplicationContext(),"Cannot rate own work",Toast.LENGTH_LONG).show();
            return;
        }
       DatabaseReference reference=FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS_DIR).child(UserId);
        listener2=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child(Constants.USER_RATING).setValue(totalRating);
                reference.removeEventListener(listener2);
                fetchRating();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addListenerForSingleValueEvent(listener2);

    }

    private void fetchRating(){
        if (!dialog.isShowing())dialog.show();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS_DIR).child(UserId);
        ratingListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(Constants.USER_RATING)){
                    reference.removeEventListener(ratingListener);
                    if(dialog.isShowing())dialog.dismiss();
                    return;
                }
             String string=dataSnapshot.child(Constants.USER_RATING).getValue().toString();
             totalRating=Integer.parseInt(string);
             currentRating=new Tagger().getRating(totalRating);
             ratingBar.setRating(currentRating);
             reference.removeEventListener(ratingListener);
             if (dialog.isShowing())dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addListenerForSingleValueEvent(ratingListener);
    }

    private void fetchLocation() {
        if (!dialog.isShowing())dialog.show();
        UserUtils.getInstance().fetchLocation(UserId, new CompleteCallBacks() {
            @Override
            public void onComplete(Object... objects) {
                UserLocation=(String)objects[0];
                locationText.setText(UserLocation);
                fetchRating();
            }

            @Override
            public void onFailure(String error) {
                if (dialog.isShowing())dialog.dismiss();
                new ItemUtils().showMessageDialog(UserActivity.this, error);
                fetchRating();

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.user_menu,menu);
        MenuItem item=menu.findItem(R.id.profilechange);
        MenuItem menuItem=menu.findItem(R.id.location);

        if (!new FirebaseUtils().isCurrentUser(UserId)){
            item.setEnabled(false);
            item.setVisible(false);
            menuItem.setEnabled(false);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.location){
            changeLocation();
        }else
            if (item.getItemId()==R.id.profilechange){
                pickImage();
            }
        else
            if (item.getItemId()==R.id.messages){
               Dialog dialog=new Dialog(UserActivity.this,R.style.CalendarTheme);
               dialog.setContentView(R.layout.fragment_notice);
               dialog.setCanceledOnTouchOutside(false);
               dialog.show();
                RecyclerView recyclerView=dialog.findViewById(R.id.recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setHasFixedSize(true);

                List<String> notificationList=new ArrayList<>();
                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Messages").child(new FirebaseUtils().getCurrentUserId());
                messageListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                           notificationList.add(dataSnapshot.child(Constants.KEY_MESSAGE).getValue().toString());
                       }
                       reference.removeEventListener(messageListener);
                       recyclerView.setAdapter(new NotificationsAdapter(notificationList));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                reference.addListenerForSingleValueEvent(messageListener);

            }

        return super.onOptionsItemSelected(item);
    }

    private void pickImage() {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"),GALLERY_REQUEST);
    }

    private void changeLocation() {
        AlertDialog.Builder builder=new AlertDialog.Builder(UserActivity.this);
        builder.setMessage("Your current GPS Location will be fetched automatically. You may need to enable GPS");
        builder.setPositiveButton("Continue", (dialog, which) -> {
            dialog.dismiss();
            continueRequest();
        }).setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            Toast.makeText(getApplicationContext(),"You have cancelled the operation",Toast.LENGTH_LONG).show();
        });
        Dialog dialog=builder.create();
        dialog.show();
    }

    private void continueRequest() {
        Dexter.withActivity(UserActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Timber.e("granted");
                        Commons.getInstance().getLocation(UserActivity.this, location -> {
                            Timber.e(location);
                            cityName=location;
                            updateLocation();
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()){
                            new ItemUtils().showMessageDialog(getApplicationContext(), "Permission for "+response.getPermissionName()+" is denied");
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
AlertDialog.Builder builder=new AlertDialog.Builder(getApplicationContext());
builder.setMessage("TradeIt needs this permission to access your location, For full functionality, the app should be allowed this permission");
builder.setPositiveButton("Continue", (dialog, which) -> {
    dialog.dismiss();
    token.continuePermissionRequest();
});
Dialog dialog=builder.create();
dialog.show();

                    }
                }).check();

    }

    private void updateLocation() {
        if (!dialog.isShowing())dialog.dismiss();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS_DIR).child(UserId);
        locationlistener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child(Constants.USER_LOCATION).setValue(cityName).addOnSuccessListener(aVoid -> {
                    reference.removeEventListener(locationlistener);
                    new ItemUtils().showMessageDialog(UserActivity.this,"Success");
                    fetchLocation();
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addListenerForSingleValueEvent(locationlistener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_REQUEST && resultCode==AppCompatActivity.RESULT_OK && data!=null && data.getData()!=null){
            filePath = data.getData();

            Timber.e(getUriFromContent(filePath));
            Reducer.compressImage(UserActivity.this, Collections.singletonList(getUriFromContent(filePath)), fileList -> uploadProfile(fileList.get(0).getAbsolutePath()));

        }
    }

    public String getUriFromContent(Uri uri){
        String filepath="";
        String wholeId= DocumentsContract.getDocumentId(uri);
        String id=wholeId.split(":")[1];
        String[] columns={MediaStore.Images.Media.DATA};
        String condition= MediaStore.Images.Media._ID+ "=?";
        Cursor cursor=getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,columns,condition,new String[]{id},null);
        int columnIndex=cursor.getColumnIndex(columns[0]);
        if (cursor.moveToFirst()){
            filepath=cursor.getString(columnIndex);
        }
        cursor.close();
        return filepath;

    }
    public String getExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadProfile(String imagePath) {
        if (!dialog.isShowing())dialog.show();
        StorageReference reference= FirebaseStorage.getInstance().getReference("Profile").child(System.currentTimeMillis()+"."+getExtension(filePath));
        UploadTask uploadTask=reference.putFile(Uri.fromFile(new File(imagePath)));

        Task<Uri> uriTask=uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()){
new ItemUtils().showMessageDialog(UserActivity.this, task.getException().getMessage());
return null;
            }
            return reference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                downloadUrl=task.getResult().toString();
                updateImageUrl();
            }
        });
    }

    private void updateImageUrl() {

DatabaseReference reference=FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS_DIR).child(UserId);
listener1=new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        reference.child(Constants.USER_IMAGE).setValue(downloadUrl).addOnSuccessListener(aVoid -> fetchUserImage());
        reference.removeEventListener(listener1);
        dialog.dismiss();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
};
reference.addValueEventListener(listener1);

    }

    //<editor-fold desc="Fetch user Image" defaultstate="collapsed">
    private void fetchUserImage() {
        UserUtils.getInstance().fetchUserImage(UserId, new CompleteCallBacks() {
            @Override
            public void onComplete(Object... objects) {
                String userImage=(String)objects[0];
                Glide.with(getApplicationContext()).load(userImage).into(userImageView);
fetchLocation();
            }

            @Override
            public void onFailure(String error) {
                if (dialog.isShowing())dialog.dismiss();
                new ItemUtils().showMessageDialog(UserActivity.this, error);
                fetchLocation();
            }
        });

    }
    //</editor-fold>
}
