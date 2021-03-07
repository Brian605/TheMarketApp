package com.returno.tradeit.activities;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.returno.tradeit.R;
import com.returno.tradeit.adapters.ViewPagerAdapter;

import java.util.Objects;

public class ModuleActivity extends AppCompatActivity {
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);

        //views
        Toolbar toolbar = findViewById(R.id.toolbar);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewpager);

        //set toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("The Market");
        }
        if (toolbar.getOverflowIcon()!=null){
            toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.color_white), PorterDuff.Mode.SRC_ATOP);
        }
        //tabsArray
        String[] tabNames=getResources().getStringArray(R.array.tab_names);

        //set the tab names
        for (String s:tabNames){
            tabLayout.addTab(tabLayout.newTab().setText(s));
        }

        //initialize viewpager adapter
        final ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        //set adapter to viewpager
        viewPager.setAdapter(adapter);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_person_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_favorite);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_notifications);
        //monitor page change
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //add tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    }

