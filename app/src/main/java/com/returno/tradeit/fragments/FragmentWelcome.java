package com.returno.tradeit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.returno.tradeit.R;
import com.returno.tradeit.activities.LoginActivity;
import com.returno.tradeit.local.PreferenceManager;
import com.returno.tradeit.utils.Constants;

public class FragmentWelcome extends Fragment {

    public FragmentWelcome() {
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
        View view= inflater.inflate(R.layout.fragment_welcome, container, false);
        MaterialButton nextBtn = view.findViewById(R.id.buttonNext1);
        nextBtn.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayouts, new FragmentPrivacy()).commit());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!PreferenceManager.getInstance().isFirstTimeLaunch(getActivity()) && PreferenceManager.getInstance().isBooleanValueTrue(Constants.POLICY_ACCEPTED,getActivity())){
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }
}