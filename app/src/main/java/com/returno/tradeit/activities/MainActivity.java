package com.returno.tradeit.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.returno.tradeit.R;
import com.returno.tradeit.adapters.CategoriesRecyclerAdapter;
import com.returno.tradeit.callbacks.CompleteCallBacks;
import com.returno.tradeit.callbacks.RecyclerCallBacks;
import com.returno.tradeit.local.PreferenceManager;
import com.returno.tradeit.models.CategoryItem;
import com.returno.tradeit.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CategoriesRecyclerAdapter adapter;
    private Dialog dialog;
    private List<CategoryItem> categoryItems;

    private String ItemTitle, ItemPrice, AuctionDate;
    private TextInputEditText TitleEdit, PriceEdit, DateEdit;
    private DatePickerDialog datePickerDialog;

    private TextView OverlayText;
    private ImageView imageView;

    private Uri ImagePath;
    private Toolbar toolbar;

    private ValueEventListener AuctionListener, ItemListener;
    private TextView options,pickView;
    List<View> views=new ArrayList<>();
    HashMap<View,String> mainTutorial =new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
           // getSupportActionBar().setTitle("");
            Objects.requireNonNull(toolbar.getOverflowIcon()).setColorFilter(getResources().getColor(R.color.colorwhite), PorterDuff.Mode.SRC_ATOP);
        }
        //new DBHelper(MainActivity.this).onUpgrade();

        Animation animIn = AnimationUtils.loadAnimation(this, R.anim.animin);
        Animation animOut = AnimationUtils.loadAnimation(this, R.anim.animout);

        // startService(new Intent(MainActivity.this, NotificationWorker.class));
        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.FlipV);
        flipper.setFlipInterval(5000);
        flipper.setAutoStart(true);
        flipper.setInAnimation(animIn);
        flipper.setOutAnimation(animOut);
        flipper.startFlipping();

        recyclerView = findViewById(R.id.recycler);
        options= findViewById(R.id.options);
        pickView=findViewById(R.id.pick);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        categoryItems = new ArrayList<>();

        views.addAll(Arrays.asList(options,pickView));
        mainTutorial.put(options,"Click here for more \n 1. Main -> View favorites,Your items , messages, go to your profile \n" +
                "2. Log out Now -> To log out of your account, you can log in anytime later \n" +
                "3. Product Requests -> View items that other users have requested for. you can then contact them if you have the items");
        mainTutorial.put(pickView," Click on a category in this list to view more and post your item");
        //Progress Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progressdialog);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Categories");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            CategoryItem categoryItem = dataSnapshot1.getValue(CategoryItem.class);
                            categoryItems.add(categoryItem);

                        }

                        adapter = new CategoriesRecyclerAdapter(getApplicationContext(), categoryItems, new RecyclerCallBacks() {

                            @Override
                            public void onItemClick(View view, int position) {

                                gotoSingleCategory(view);

                            }
                        });
                        recyclerView.setAdapter(adapter);

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        setUpShowCase();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        });
        thread.start();


    }

    private void setUpShowCase() {
     if (PreferenceManager.getInstance(this).isBoleanValueTrue(Constants.IS_MAIN_FIRST_LAUNCH)){

         showCase(views.get(0),new CompleteCallBacks() {
             @Override
             public void onComplete(Object... objects) {
                 if (!views.isEmpty()){
                     setUpShowCase();
                 }
             }
         });
         disableNextShowCase();
     }
    }

    private void disableNextShowCase() {
       PreferenceManager.getInstance(this).storeBooleanValue(Constants.IS_MAIN_FIRST_LAUNCH,false);
    }

    private void showCase(View view, CompleteCallBacks callBacks) {

           ShowcaseView.Builder builder = new ShowcaseView.Builder(this);
           ViewTarget viewTarget = new ViewTarget(view);
           builder.setTarget(viewTarget)
                   .setContentTitle("Tips")
                   .setContentText(mainTutorial.get(view))
                   .withHoloShowcase()
                   .setStyle(R.style.ShowCaseGreen)
                   .setShowcaseEventListener(new OnShowcaseEventListener() {
                       @Override
                       public void onShowcaseViewHide(ShowcaseView showcaseView) {
                          // showcaseView.hide();
                           views.remove(view);
callBacks.onComplete(null);

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



    private void gotoSingleCategory(View view) {
        TextView categoryId;
        String category;
        categoryId = view.findViewById(R.id.categ);
        category = categoryId.getText().toString();

        Intent intent = new Intent(MainActivity.this, CategoryViewActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        // View view=(View)menu.findItem(R.id.auction);
        //registerForContextMenu(view);
        if (menu instanceof MenuBuilder) {
            MenuBuilder builder = (MenuBuilder) menu;
            builder.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout1) {
            //logout
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));

        } else if (id == R.id.main) {
            startActivity(new Intent(MainActivity.this, ModuleActivity.class));
        } else if (id == R.id.request) {
            viewRequests();
        }
        return true;
    }

    private void viewRequests() {
        Intent intent = new Intent(MainActivity.this, RequestsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog!=null && dialog.isShowing())dialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
