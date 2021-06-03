package com.returno.tradeit.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.returno.tradeit.R;
import com.returno.tradeit.activities.MainActivity;
import com.returno.tradeit.local.PreferenceManager;
import com.returno.tradeit.utils.Commons;
import com.returno.tradeit.utils.Constants;
import com.returno.tradeit.utils.FirebaseUtils;
import com.returno.tradeit.utils.ItemUtils;

import org.jetbrains.annotations.NotNull;

public class FragmentPrivacy extends Fragment {
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
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        container.removeAllViews();
        if (!FirebaseUtils.isInternetAvailable()){
            new ItemUtils().showMessageDialog(getActivity(),"No Internet. Check your connection and try again");
        }
        View view= inflater.inflate(R.layout.fragment_privacy, container, false);

        Dialog dialog=new Dialog(getActivity());
        dialog.setContentView(R.layout.circular_progress_dialog);
        TextView textView=dialog.findViewById(R.id.progress);
        textView.setVisibility(View.GONE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WebView webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (!dialog.isShowing() && newProgress<100){
                    dialog.show();
                }else {
                    dialog.dismiss();
                }
            }
        });
        webView.setWebViewClient(new MyClient(dialog,getActivity()));
        webView.loadUrl("https://themarket.co.ke/privacy-terms.html");


        Button agree = view.findViewById(R.id.agree);
        Button decline = view.findViewById(R.id.decline);

        agree.setOnClickListener(v -> {
            PreferenceManager.getInstance().storeBooleanValue(Constants.POLICY_ACCEPTED,true,getActivity());
if (Commons.from.equals("categ")){

    startActivity(new Intent(getActivity(), MainActivity.class));

}else {
    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayouts, new FragmentSafety()).commit();
}

        });

        decline.setOnClickListener(v -> System.exit(0));
        return view;
    }

}

class MyClient extends WebViewClient{
    final Context context;
    Dialog dialog;
    public MyClient(Dialog dialog, Context context){
        this.context=context;
        this.dialog=dialog;

    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (!dialog.isShowing())dialog.show();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String request) {
        view.loadUrl(request);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (dialog.isShowing())dialog.dismiss();
    }
}