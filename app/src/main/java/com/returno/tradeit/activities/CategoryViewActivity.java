package com.returno.tradeit.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.returno.tradeit.R;
import com.returno.tradeit.adapters.ItemRecyclerAdapter;
import com.returno.tradeit.callbacks.CounterCallBacks;
import com.returno.tradeit.callbacks.FetchCallBacks;
import com.returno.tradeit.callbacks.RecyclerCallBacks;
import com.returno.tradeit.fragments.FragmentPrivacy;
import com.returno.tradeit.local.DatabaseManager;
import com.returno.tradeit.models.Item;
import com.returno.tradeit.utils.Commons;
import com.returno.tradeit.utils.Constants;
import com.returno.tradeit.utils.ItemUtils;
import com.returno.tradeit.utils.UploadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class CategoryViewActivity extends AppCompatActivity  {
    private RecyclerView recyclerView;
    private ArrayList<Item> tradeItList;
    private ItemRecyclerAdapter adapter;
    private String category;
    private ValueEventListener listener=null,listener1;
    private DatabaseReference countingReference;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_activity);


        TextView categoryText = findViewById(R.id.categoryText);
        Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton floatingActionButton = findViewById(R.id.addNew);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(CategoryViewActivity.this,R.drawable.ic_arrow_ios));
            Objects.requireNonNull(toolbar.getOverflowIcon()).setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(CategoryViewActivity.this,R.color.whiteTransparent), PorterDuff.Mode.SRC_ATOP));
        }

        //get the appropriate category
        Intent intent = getIntent();
        category = intent.getStringExtra(Constants.ITEM_CATEGORY);

        Timber.e(category);
        if (category == null) {
            return;
        }
        categoryText.setText(category);

        floatingActionButton.setOnClickListener(v -> gotoAddNew());
        
        tradeItList = new ArrayList<>();
        recyclerView = findViewById(R.id.category_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        AlertDialog.Builder builder = new AlertDialog.Builder(CategoryViewActivity.this);
        builder.setView(R.layout.progressdialog);
         dialog = builder.create();
        dialog.show();


        adapter = new ItemRecyclerAdapter(false, CategoryViewActivity.this, tradeItList, new RecyclerCallBacks() {
            @Override
            public void onItemClick(View view, int position) {
                ItemUtils.goToSingleView(view, CategoryViewActivity.this, category, Constants.MODE_LOCAL);
            }

            @Override
            public void onLongClick(View view, int position) {
                TextView categoryView=view.findViewById(R.id.itemCategory);
                ItemUtils.share(view,CategoryViewActivity.this,categoryView.getText().toString());

            }
        });

        recyclerView.setAdapter(adapter);

        checkCounts();

    }


    private void checkCounts(){
new UploadUtils().getOnlineItemsCount(category, new CounterCallBacks() {
    @Override
    public void counterResult(int count) {
        DatabaseManager manager=new DatabaseManager(CategoryViewActivity.this).open();
        int localCount=(int)manager.getItemCount(category);
        manager.close();

        int difference=count-localCount;
        decideFetch(difference);
    }

    @Override
    public void noData() {
        Toast.makeText(getApplicationContext(),"No data available for this category ",Toast.LENGTH_LONG).show();
        if (dialog.isShowing())dialog.dismiss();
    }

    @Override
    public void onError(String message) {
new ItemUtils().showMessageDialog(getApplicationContext(),message);
    }
});

    }

    private void decideFetch(int difference){
       if (difference>0){
           fetchDetails(difference);
       }else {
           if (dialog.isShowing())dialog.dismiss();
           setAdapter();
       }
    }

    private void fetchDetails(int difference) {
new UploadUtils().fetchItems(category, difference, new FetchCallBacks() {
    @Override
    public void fetchComplete(List<Item> itemList) {
ItemUtils.storeFileFromOnlineUrl(CategoryViewActivity.this, itemList, dirs -> setAdapter());
    }

    @Override
    public void fetchError(String message) {
if (dialog.isShowing())dialog.dismiss();
Timber.e(message);
        Toast.makeText(CategoryViewActivity.this, message, Toast.LENGTH_SHORT).show();
    }
});

    }

    private void setAdapter() {
        DatabaseManager manager=new DatabaseManager(getApplicationContext()).open();
        tradeItList.clear();
        tradeItList.addAll(manager.getProducts(category));
        manager.close();

       adapter.notifyDataSetChanged();
if (dialog.isShowing()){
    dialog.dismiss();
}
       if (tradeItList.size()>2){
       recyclerView.scrollToPosition(tradeItList.size()-2);
       }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.categorymenu,menu);

        MenuItem item=menu.findItem(R.id.search_bar);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setBackground(ContextCompat.getDrawable(CategoryViewActivity.this,R.drawable.searchback));

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

    private void gotoAddNew(){
        Intent intent=new Intent(CategoryViewActivity.this, ItemPostActivity.class);
        intent.putExtra(Constants.ITEM_CATEGORY,category);
        startActivity(intent);
        finish();
        Timber.e(String.valueOf(super.onSupportNavigateUp()));
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       int id=item.getItemId();

       if (id==android.R.id.home) {
           finish();
        }
       if (id==R.id.logout){
          //sign out user
           FirebaseAuth.getInstance().signOut();
           startActivity(new Intent(this,LoginActivity.class));
       }else
           if (id==R.id.newPost){

               //Go to a new post activity
              gotoAddNew();
           }else
               if (id==R.id.refresh){
                 this.recreate();
               }
               else
               if (id==R.id.termsMenu){
                   Commons.from="categ";
                   FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                   transaction.replace(R.id.relativeL,new FragmentPrivacy());
                   transaction.commit();
                   transaction.addToBackStack(null);
               }
           else {
               super.onOptionsItemSelected(item);
           }

       return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog!=null || dialog.isShowing())dialog.dismiss();
    }


}
