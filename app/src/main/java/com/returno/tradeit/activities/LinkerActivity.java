package com.returno.tradeit.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.returno.tradeit.utils.Constants;

public class LinkerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Uri deepLink;
                    if (pendingDynamicLinkData!=null){
                        deepLink=pendingDynamicLinkData.getLink();

                        assert deepLink != null;
                        String itemid=deepLink.getQueryParameter(Constants.ITEM_ID);
                        String userid=deepLink.getQueryParameter(Constants.USER_ID);
                        String title=deepLink.getQueryParameter(Constants.ITEM_TITLE);
                        String descrip=deepLink.getQueryParameter(Constants.ITEM_DESCRIPTION);
                        String image=deepLink.getQueryParameter(Constants.ITEM_IMAGE);
                        String price=deepLink.getQueryParameter(Constants.ITEM_PRICE);
                        String category=deepLink.getQueryParameter(Constants.ITEM_CATEGORY);
                        String tags=deepLink.getQueryParameter(Constants.ITEM_TAG);

                        Intent intent=new Intent(LinkerActivity.this,SingLeItemActivity.class);
                        intent.putExtra(Constants.POSTER_ID,userid);
                        intent.putExtra(Constants.ITEM_IMAGE,image);
                        intent.putExtra(Constants.ITEM_TITLE,title);
                        intent.putExtra(Constants.ITEM_DESCRIPTION,descrip);
                        intent.putExtra(Constants.ITEM_PRICE,price);
                        intent.putExtra(Constants.ITEM_ID,itemid);
                        intent.putExtra(Constants.ITEM_CATEGORY,category);
                        intent.putExtra(Constants.ITEM_TAG,tags);
                        intent.putExtra(Constants.MODE,Constants.MODE_LINK);
                        startActivity(intent);


                    }
                });

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
