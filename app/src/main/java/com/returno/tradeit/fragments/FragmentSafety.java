package com.returno.tradeit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.returno.tradeit.R;
import com.returno.tradeit.activities.LoginActivity;
import com.returno.tradeit.adapters.SafetyAdapter;
import com.returno.tradeit.local.PreferenceManager;
import com.returno.tradeit.utils.Constants;


public class FragmentSafety extends Fragment {


    public FragmentSafety() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_safety, container, false);
        ListView listView=view.findViewById(R.id.listView);
        String[] items=getResources().getStringArray(R.array.safety_list);
        SafetyAdapter adapter=new SafetyAdapter(items,getActivity());
        listView.setAdapter(adapter);

        MaterialButton materialButton=view.findViewById(R.id.buttonNext2);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getInstance(getActivity()).storeBooleanValue(Constants.IS_MAIN_FIRST_LAUNCH,false);
                PreferenceManager.getInstance(getActivity()).storeBooleanValue(Constants.FIRST_TIME_LAUNCH,false);
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        return view;
    }
}