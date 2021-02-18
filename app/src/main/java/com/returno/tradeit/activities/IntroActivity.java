package com.returno.tradeit.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.returno.tradeit.R;
import com.returno.tradeit.fragments.FragmentWelcome;

public class IntroActivity extends AppCompatActivity {
private LinearLayout rootLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        rootLayout=findViewById(R.id.fragmentLayouts);
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.fragmentLayouts, new FragmentWelcome()).commit();
        transaction.addToBackStack(null);

    }
}