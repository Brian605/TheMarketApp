package com.returno.tradeit.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opensooq.supernova.gligar.GligarPicker;
import com.returno.tradeit.R;
import com.returno.tradeit.callbacks.UploadCallBacks;
import com.returno.tradeit.models.Item;
import com.returno.tradeit.models.Notification;
import com.returno.tradeit.utils.FirebaseUtils;
import com.returno.tradeit.utils.ItemUtils;
import com.returno.tradeit.utils.Reducer;
import com.returno.tradeit.utils.Tagger;
import com.returno.tradeit.utils.UploadUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

@SuppressWarnings("ConstantConditions")
public class ItemPostActivity extends AppCompatActivity  {
    // imports
    private ImageView imageBtn;
    private static final int GALLERY_REQUEST_CODE = 2;
    private TextInputEditText titleEdit;
    private TextInputEditText descEdit;
    private String PostPrice;
    private static final int MY_PERMISSIONS_READ_STORAGE =0;
    private String PostTitle;
    private String PostDesc;
    private String tagString;
    private TextInputEditText priceEdit;
    private String category;
    private TextView hintView,tagView;
    private ValueEventListener postListener;
 private StringBuilder tag;
 private boolean isFirst=true;
 private Dialog dialog;
    private TextView progressView;
    private List<String> pathList;
    private LinearLayout selectedItemsLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_post);
        Intent intent=getIntent();
        category=intent.getStringExtra("category");
        if (category == null){
            category="electronics";
        }
        pathList=new ArrayList<>();

        // initializing views
        Button postBtn = findViewById(R.id.postBtn);
        descEdit = findViewById(R.id.textDesc);
        titleEdit= findViewById(R.id.textTitle);
        priceEdit=findViewById(R.id.itemprice);
        hintView=findViewById(R.id.textview);
        tagView=findViewById(R.id.tagText);
        Button tagButton = findViewById(R.id.addTag);
        selectedItemsLayout=findViewById(R.id.selectedItems);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_ios));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }



        tag=new StringBuilder();

        //Get an instance of the current user
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Get the current username
       FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        imageBtn = findViewById(R.id.imageBtn);

        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] items=getResources().getStringArray(R.array.tag_keys);
                AlertDialog.Builder builder=new AlertDialog.Builder(ItemPostActivity.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isFirst){
                            tag.append(items[which]);
                            isFirst=false;
                        }else {
                            tag.append("__").append(items[which]);
                        }
                        tagView.setText(tag.toString());
                        dialog.dismiss();
                    }
                });

                Dialog dialog=builder.create();
                dialog.show();

            }
        });
        //<editor-fold defaultstate="collapsed" desc="picking image from gallery">
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               new GligarPicker().requestCode(GALLERY_REQUEST_CODE).withActivity(ItemPostActivity.this).limit(3).show();
            }
        });
        // </editor-fold>


        //< editor-fold desc=" posting the image and details to firebase to Firebase">
        postBtn.setOnClickListener(view -> {
            //Views
            PostTitle = titleEdit.getText().toString().trim();
            PostDesc = descEdit.getText().toString().trim();
           // Log.e("desc",PostDesc);
            PostPrice=priceEdit.getText().toString().trim();

            tagString=tagView.getText().toString().trim();
            if (tagString.equals("No Tags Yet") || TextUtils.isEmpty(tagString)){
                tagString=category;
            }

            // do a check for empty fields
            if (!TextUtils.isEmpty(PostDesc) && !TextUtils.isEmpty(PostTitle) && !TextUtils.isEmpty(PostPrice)) {


                if (pathList.isEmpty()) {
                    Toast.makeText(ItemPostActivity.this, "Please select an Image. ", Toast.LENGTH_LONG).show();
                    Tagger.forceFocus(imageBtn);
                    return;
                }

                //Request storage permisssion
                if (ContextCompat.checkSelfPermission(ItemPostActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    //no permission handle permission requests
                    ActivityCompat.requestPermissions(ItemPostActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_READ_STORAGE);
                }else{
                    //Permission already granted upload
                    uploadData();
                }

                //end permission request

            }//end check fields

        });

    }

    public void uploadData() {

        View view=LayoutInflater.from(ItemPostActivity.this).inflate(R.layout.uploaddialog,null,false);
        dialog=new Dialog(ItemPostActivity.this);
        dialog.setContentView(view);
        progressView=view.findViewById(R.id.progress);
        dialog.show();

        Reducer.compressImage(ItemPostActivity.this, pathList, fileList -> {
            String itemId=ItemUtils.generateItemId();
    new UploadUtils()
            .uploadItem(new Item(itemId, PostTitle, PostDesc, Integer.parseInt(PostPrice), tagString, StringUtils.join(fileList, "___"), new FirebaseUtils().getCurrentUserId(), category),
                    new UploadCallBacks() {
                        @Override
                        public void onUploadSuccess() {
if (dialog.isShowing())dialog.dismiss();
Toast.makeText(getApplicationContext(),"Upload Success",Toast.LENGTH_LONG).show();
ItemUtils.clearUploadCache();
                            Notification notification=new Notification(itemId,PostPrice,"products",PostTitle,null);
                            new FirebaseUtils().postAPushNotification(notification);
                     pathList.clear();
                     selectedItemsLayout.removeAllViewsInLayout();
                     priceEdit.setText("");
                     titleEdit.setText("");
                     descEdit.setText("");
                     tagView.setText("");
                     tag=null;
                     tag=new StringBuilder();
                        }

                        @Override
                        public void onProgressUpdated(int newValue) {
progressView.setText(String.format(Locale.getDefault(),"Uploading %d %%", newValue));
                        }

                        @Override
                        public void onError(String message) {
                            Timber.e(message);
                            if (dialog.isShowing())dialog.dismiss();
                            new ItemUtils().showMessageDialog(ItemPostActivity.this,0,false,message);
                             }
                    });

        });

    }
    //<editor-fold desc="Getting the image from gallery">
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode!=AppCompatActivity.RESULT_OK){
            return;
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            pathList.clear();
            selectedItemsLayout.removeAllViewsInLayout();
            String[] paths=data.getExtras().getStringArray(GligarPicker.IMAGES_RESULT);
            pathList.addAll(Arrays.asList(paths));

            for (String path:pathList) {
                View view = LayoutInflater.from(ItemPostActivity.this).inflate(R.layout.singleimageview, null, false);
                AppCompatImageView itemImage = view.findViewById(R.id.imageItem);
                TextView uriView = view.findViewById(R.id.uriHolder);
                uriView.setText(path);
                itemImage.setImageURI(Uri.fromFile(new File(path)));
                selectedItemsLayout.addView(view);
                view.getLayoutParams().width = (int) getResources().getDimension(R.dimen.image_item_max_width);
                view.requestLayout();

                itemImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChangeDialog(uriView.getText().toString(), view);
                    }
                });
                CircleImageView circleImageView = view.findViewById(R.id.deleteImage);
                circleImageView.setFocusable(true);
                circleImageView.setClickable(true);

                circleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedItemsLayout.removeView(view);
                        TextView textView1 = view.findViewById(R.id.uriHolder);
                        String uri = textView1.getText().toString();
                        int currentPosition = pathList.indexOf(uri);
                        pathList.remove(uri);

                        if (pathList.size() != 0) {
                            imageBtn.setImageURI(Uri.fromFile(new File(pathList.get(0))));
                        } else {
                            imageBtn.setImageDrawable(ContextCompat.getDrawable(ItemPostActivity.this, R.drawable.img7));
                        }

                    }
                });
            }

            imageBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
            hintView.setVisibility(View.GONE);
        }
    }

    private void showChangeDialog(String uriPath, View view) {
       View view1=LayoutInflater.from(ItemPostActivity.this).inflate(R.layout.image_dialog,null,false);
       ImageView imageView=view1.findViewById(R.id.imageItem);
        RelativeLayout relativeLayout=view1.findViewById(R.id.edit);
        imageView.setImageURI(Uri.fromFile(new File(uriPath)));

       AlertDialog.Builder builder=new AlertDialog.Builder(ItemPostActivity.this);
       builder.setView(view1);
       Dialog dialog=builder.create();
       dialog.show();
    }


    //</editor-fold>


}
