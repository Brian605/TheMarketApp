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

import com.erkutaras.showcaseview.ShowcaseManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.returno.tradeit.R;
import com.returno.tradeit.adapters.CategoriesRecyclerAdapter;
import com.returno.tradeit.callbacks.RecyclerCallBacks;
import com.returno.tradeit.local.PreferenceManager;
import com.returno.tradeit.models.CategoryItem;
import com.returno.tradeit.services.FirebaseService;
import com.returno.tradeit.utils.Constants;

import java.util.ArrayList;
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
    private View recyclerShowView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //hide the default actionB
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");

        getApplicationContext().startService(new Intent(getApplicationContext(), FirebaseService.class));
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.whiteTransparent));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
           // getSupportActionBar().setTitle("");
            Objects.requireNonNull(toolbar.getOverflowIcon()).setColorFilter(getResources().getColor(R.color.colorwhite), PorterDuff.Mode.SRC_ATOP);
        }
        //new DBHelper(MainActivity.this).onUpgrade();

        Animation animin = AnimationUtils.loadAnimation(this, R.anim.animin);
        Animation animout = AnimationUtils.loadAnimation(this, R.anim.animout);

        // startService(new Intent(MainActivity.this, NotificationWorker.class));
        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.FlipV);
        flipper.setFlipInterval(5000);
        flipper.setAutoStart(true);
        flipper.setInAnimation(animin);
        flipper.setOutAnimation(animout);
        flipper.startFlipping();

        recyclerView = findViewById(R.id.recycler);
        recyclerShowView= findViewById(R.id.mainRecyclerShowcase);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        categoryItems = new ArrayList<>();

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
         showCase();
         disableNextShowCase();
     }
    }

    private void disableNextShowCase() {
       // PreferenceManager.getInstance(this).storeBooleanValue(Constants.IS_MAIN_FIRST_LAUNCH,false);
    }

    private void showCase() {
        ShowcaseManager.Builder builder=new ShowcaseManager.Builder();
        builder.context(MainActivity.this)
                .view(recyclerShowView)
                .descriptionText("You can choose an item in the list to begin")
                .key("KEY")
                .descriptionImageRes(R.drawable.ic_baseline_help_outline_24)
                .buttonText("OKAY")
                .colorButtonBackground(R.color.loginheader)
                .add()
                .build()
                .show();
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
