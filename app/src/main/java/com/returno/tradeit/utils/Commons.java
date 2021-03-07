package com.returno.tradeit.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.returno.tradeit.callbacks.LocationCallBacks;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration;
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.configuration.PermissionConfiguration;
import com.yayandroid.locationmanager.listener.LocationListener;

import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class Commons {
    private static Commons commons;
    public static final List<View> listOfViews = new ArrayList<>();
    public static final int previousIndex = 0;

    public static Commons getInstance() {
        if (commons == null) {
            commons = new Commons();
        }
        return commons;
    }



    public void getLocation(AppCompatActivity activity, LocationCallBacks callBacks) {

        LocationConfiguration configuration=new LocationConfiguration.Builder()
                .keepTracking(false)
                .askForPermission(new PermissionConfiguration.Builder().build())
                .useGooglePlayServices(new GooglePlayServicesConfiguration.Builder().build())
                .useDefaultProviders(new DefaultProviderConfiguration.Builder().build()).build();

        LocationManager manager=new LocationManager.Builder(activity.getApplicationContext())
                .activity(activity)
                .configuration(configuration)
                .notify(new LocationListener() {
                    @Override
                    public void onProcessTypeChanged(int processType) {

                    }

                    @Override
                    public void onLocationChanged(Location location) {
Timber.e(location.toString());
callBacks.onLocation(geoCode(location,activity));
                    }

                    @Override
                    public void onLocationFailed(int type) {
new ItemUtils().showMessageDialog(activity,"Could not fetch your location");
                    }

                    @Override
                    public void onPermissionGranted(boolean alreadyHadPermission) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                })
                .build();
        manager.get();

    }

    private String geoCode(Location location, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                String cityName = address.getAddressLine(0) + ", " + address.getLocality() + ", " + address.getSubLocality();
                Timber.e(cityName);

                return cityName;
            }
            Timber.e("Null address list");

            return null;
        } catch (IOException e) {
            Toast.makeText(context, "Could not decode location try again", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public boolean isValidUserName(String userName) {
        String regex = "^[\\p{L} .'-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userName);
        return matcher.matches();
    }

    public boolean isValidEmail(String email) {
        String regex = "^.+@.+\\..+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public View viewGetNextView() {
        if (previousIndex >= listOfViews.size() - 1) {
            return null;
        }
        return listOfViews.get(previousIndex + 1);
    }

    public void sendMessage(final String sms, String phone, Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
        intent.putExtra("sms_body", sms);
        context.startActivity(intent);
    }

    public void openWhatsApp(Context context, String userPhone) throws UnsupportedEncodingException {
        userPhone = Tagger.getInternationalNumber(userPhone);
        String message = URLEncoder.encode("Hello , there", "UTF-8");
        String url = "https://wa.me/" + userPhone + "?text=" + message;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public void callUser(Context context, String userPhone) {
        Timber.e("callUser");
        Timber.e(userPhone);

        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + userPhone));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(((AppCompatActivity)context),new String[]{Manifest.permission.CALL_PHONE},Constants.PERMISSION_CALL_PHONE);
                 return;
            }
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
