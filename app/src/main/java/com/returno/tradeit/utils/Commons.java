package com.returno.tradeit.utils;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dmgdesignuk.locationutils.easylocationutility.EasyLocationUtility;
import com.dmgdesignuk.locationutils.easylocationutility.LocationRequestCallback;
import com.returno.tradeit.callbacks.LocationCallBacks;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class Commons {
    private static Commons commons;
    public static List<View> listOfViews = new ArrayList<>();
    public static int previousIndex = 0;

    public static Commons getInstance() {
        if (commons == null) {
            commons = new Commons();
        }
        return commons;
    }

    public boolean checkLocationEnabled(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage("Your gps is off, Click ok to turn it on")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        Dialog dialog = builder.create();

        return false;

    }

    public void getLocation(AppCompatActivity activity, LocationCallBacks callBacks) {

        EasyLocationUtility easyLocationUtility = new EasyLocationUtility(activity);
        if (!checkEasyPermissions(easyLocationUtility)) {
            Timber.e("Not enabled");
            return;
        }

        easyLocationUtility.getSmartLocation(new LocationRequestCallback() {
            @Override
            public void onLocationResult(Location location) {
                Timber.e(location.toString());
                callBacks.onLocation(geoCode(location, activity));
            }

            @Override
            public void onFailedRequest(String s) {
                Timber.e(s);
                new ItemUtils().showMessageDialog(activity, 0, false, s);
            }
        });

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
            return null;
        } catch (IOException e) {
            Toast.makeText(context, "Could not decode location try again", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private boolean checkEasyPermissions(EasyLocationUtility utility) {
        if (utility.permissionIsGranted()) {
            return true;
        }
        utility.requestPermission(100);
        return false;
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

    public void openWhatsApp(Context context, String userphone) throws UnsupportedEncodingException {
        userphone = Tagger.getInternationalNumber(userphone);
        String message = URLEncoder.encode("Hello , there", "UTF-8");
        String url = "https://wa.me/" + userphone + "?text=" + message;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public void callUser(Context context, String userphone) {
        Timber.e("callUser");
        Timber.e(userphone);

        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + userphone));
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