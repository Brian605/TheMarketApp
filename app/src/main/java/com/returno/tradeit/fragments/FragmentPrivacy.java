package com.returno.tradeit.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.returno.tradeit.R;
import com.returno.tradeit.local.PreferenceManager;
import com.returno.tradeit.utils.Constants;

public class FragmentPrivacy extends Fragment {
private WebView webView;
private Button agree,decline;
    public FragmentPrivacy() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getActivity().getIntent();


    }

    @SuppressLint("SetJavascriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_privacy, container, false);
        LinearProgressIndicator progressBar=view.findViewById(R.id.progress);
        webView=view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (progressBar.getVisibility()!=View.GONE && newProgress<100){
                    progressBar.setProgress(newProgress);
                }
            }
        });
        webView.setWebViewClient(new MyClient(progressBar,getActivity()));
        webView.loadUrl("https://themarket.co.ke/privacy-terms.html");

        agree=view.findViewById(R.id.agree);
        decline=view.findViewById(R.id.decline);

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getInstance().storeBooleanValue(Constants.POLICY_ACCEPTED,true,getActivity());
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayouts,new FragmentSafety()).commit();


            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
        return view;
    }

}

class MyClient extends WebViewClient{
    Context context;
    LinearProgressIndicator progressBar;
    public MyClient(LinearProgressIndicator progressBar, Context context){
        this.context=context;
        this.progressBar=progressBar;

    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String request) {
        view.loadUrl(request);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progressBar.setVisibility(View.GONE);
    }
}