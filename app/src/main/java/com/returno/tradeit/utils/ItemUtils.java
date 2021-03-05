package com.returno.tradeit.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.agrawalsuneet.dotsloader.loaders.PullInLoader;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.bumptech.glide.Glide;
import com.facebook.common.util.Hex;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.plattysoft.leonids.ParticleSystem;
import com.returno.tradeit.R;
import com.returno.tradeit.activities.SingLeItemActivity;
import com.returno.tradeit.callbacks.CompleteCallBacks;
import com.returno.tradeit.callbacks.DownloadCallBacks;
import com.returno.tradeit.callbacks.FetchCallBacks;
import com.returno.tradeit.local.DatabaseManager;
import com.returno.tradeit.models.Item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import timber.log.Timber;

public class ItemUtils {
    private static Item itemCopy;
    private static int counter = 0;
    private int count = 0;
    private static Dialog dialog;
    static final String favoritesFile = "favorites.ser";

    //Creates
    public static synchronized void storeFileFromOnlineUrl(Context context, List<Item> itemList, DownloadCallBacks callBacks) {
        int size = itemList.size();

        File file = new File(Constants.IMAGE_STORAGE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (Item item : itemList) {
                    String singleUrl = getSingleUrlFromString(item.getItemImage());
                    Timber.e("singleUrl " + singleUrl);
                    String urlToLoad = Urls.BASE_URL + singleUrl;
                    AndroidNetworking.download(urlToLoad, Constants.IMAGE_STORAGE_DIR, getImageName(singleUrl))
                            .setPriority(Priority.HIGH)
                            .build()
                            .startDownload(new DownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    String itemNewImageUri = Constants.IMAGE_STORAGE_DIR + "/" + getImageName(singleUrl);
                                    itemNewImageUri = itemNewImageUri + "___" + item.getItemImage();
                                    itemCopy = item;
                                    itemCopy.setItemImage(itemNewImageUri);
                                    DatabaseManager manager = new DatabaseManager(context).open();
                                    manager.insertItems(itemCopy, itemCopy.getItemCategory());
                                    counter++;
                                    if (counter == size) {
                                        callBacks.onComplete(null);
                                    }

                                }

                                @Override
                                public void onError(ANError anError) {

                                }
                            });
                }
            }
        });
        thread.start();


    }

    public static String getSingleUrlFromString(String s) {
        if (s.contains("__")) {
            return s.split("__")[0];
        }
        return s;
    }

    public static void clearUploadCache() {
        File file=new File(Reducer.getCompressDir());
        if (file.exists()){
          boolean delete=file.delete();
        }
    }

    @SuppressLint("InflateParams")
    public void showMessageDialog(Context context, int theme, boolean isThemed, String message) {
        View view=LayoutInflater.from(context).inflate(R.layout.toast_view,null,false);
        TextView messageView=view.findViewById(R.id.body);
        messageView.setText(message);
     Toast toast=new Toast(context);
     toast.setDuration(Toast.LENGTH_LONG);
     toast.setView(view);
     toast.show();
    }

    public static String getImageName(String s) {
        return s.split("/")[1];

    }

    public static String saveProfileImage(Context context, String file) {
        String images = Constants.PROFILE_IMAGE_DIR;
        String name = System.currentTimeMillis() + ".PNG";

        try {


            File storageDir = new File(Environment.getExternalStorageDirectory().getPath(), images);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            String filePath = storageDir.getAbsolutePath() + "/" + name;

            Timber.e(filePath);
            File newFile = new File(filePath);

            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(file);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }

            reference.getFile(newFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "One or more files not Downloaded", Toast.LENGTH_SHORT).show();
                }
            });

            return name;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isItemInFavorites(String id) {
        List<String> items = getLastFavoritesList();
        for (String s : items) {
            if (s.equals(id)) {
                return true;
            }
        }
        return false;
    }

    public static void addToFavorites(String itemId) {
        List<String> items = getLastFavoritesList();
        items.add(itemId);
        String filePath = "TradeIt/Files";

        File favFile = new File(Environment.getExternalStorageDirectory().getPath(), filePath);

        try {
            if (!favFile.exists()) {
                boolean success = favFile.mkdirs();
            }

            File newfavFile = new File(favFile.getAbsolutePath() + "/" + favoritesFile);

            if (newfavFile.exists()) {
                boolean deleted = newfavFile.delete();
                boolean created = newfavFile.createNewFile();
            } else {
                boolean created = newfavFile.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(newfavFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(items);

            objectOutputStream.flush();
            objectOutputStream.close();

            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeFromFavorites(String item) {
        List<String> items = getLastFavoritesList();
        items.remove(item);
        Timber.e(String.valueOf(items.size()));
        String filePath = "TradeIt/Files";

        File favFile = new File(Environment.getExternalStorageDirectory().getPath(), filePath);

        try {
            if (!favFile.exists()) {
                boolean createDirs = favFile.mkdirs();

            }

            File newfavFile = new File(favFile.getAbsolutePath() + "/" + favoritesFile);

            if (newfavFile.exists()) {
                boolean deleted = newfavFile.delete();
            }
            boolean created = newfavFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(newfavFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(items);

            objectOutputStream.flush();
            objectOutputStream.close();

            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getLastFavoritesList() {
        List<String> itemList;
        String filePath = Constants.ROOT_DIRECTORY_LOCAL + "Files";

        File favFile = new File(filePath);
        try {
            if (!favFile.exists()) {
                boolean success = favFile.mkdirs();
                   }

            File newfavFile = new File(favFile.getAbsolutePath() + "/" + favoritesFile);

            if (!newfavFile.exists()) {
                boolean isOk = newfavFile.createNewFile();
            }

            FileInputStream inputStream = new FileInputStream(newfavFile);

            if (newfavFile.length()<=0){
                return Collections.emptyList();
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            itemList = (List<String>) objectInputStream.readObject();

            inputStream.close();
            objectInputStream.close();
            return itemList;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            itemList = new ArrayList<>();
            return itemList;
        }


    }

    public static void share(View view, Context context,String category) {
        TextView uidView, itemImageView, itemTitleView, itemDescView, itemPriceView, itemIdView, tagsView;

        uidView = view.findViewById(R.id.postUserId);
        itemIdView = view.findViewById(R.id.itemId);
        itemImageView = view.findViewById(R.id.itemImageUrl);
        itemTitleView = view.findViewById(R.id.itemTitle);
        itemDescView = view.findViewById(R.id.itemDescription);
        itemPriceView = view.findViewById(R.id.itemPrice);
        tagsView = view.findViewById(R.id.tagsView);

        final String userId, itemTitle, imageUrl, itemDesc, itemPrice, itemId, itemTags;

        userId = uidView.getText().toString();
        imageUrl = itemImageView.getText().toString();
        itemTitle = itemTitleView.getText().toString();
        itemDesc = itemDescView.getText().toString();
        itemPrice = itemPriceView.getText().toString();
        itemId = itemIdView.getText().toString();
        itemTags = tagsView.getText().toString();

List<String> imagesList=getExtraImagesUri(getExtraImagesString(imageUrl));
Timber.e(imagesList.get(0));
        final String link = "https://tradeitgoods.com/items?itemid=" + itemId + "&userid=" + userId + "&image=" + imageUrl + "&title=" + itemTitle + "&descrip=" + itemDesc + "&price=" + itemPrice +"&tag="+itemTags+"&category=" +category;
        Task<ShortDynamicLink> dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://tradit.page.link")
                //.setAndroidParameters(new DynamicLink.AndroidParameters().Builder().build())
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                        .setImageUrl(Uri.parse(Urls.BASE_URL+imagesList.get(0)))
                        .setTitle(itemTitle + " " + itemPrice)
                        .setDescription("Buy and sell on your own terms. Only at TheMarket app")
                        .build())
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnCompleteListener((OnCompleteListener<ShortDynamicLink>) task -> {
                    if (task.isSuccessful()) {
                        Uri uri = Objects.requireNonNull(task.getResult()).getShortLink();
                        Uri prevLink = task.getResult().getPreviewLink();

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        assert uri != null;
                        intent.putExtra(Intent.EXTRA_TEXT, uri.toString());
                        context.startActivity(Intent.createChooser(intent, "Share With.."));
                        assert prevLink != null;
                        Timber.e(uri.toString() + "    " + prevLink.toString());

                    } else {
                        Timber.e(task.getException());
                    }
                });
        //Uri uri=dynamicLink.getUri();


    }

    public static String getLocalImageUri(String completeUri) {
        return completeUri.split("___")[0];
    }

    //<editor-fold defaultstate="collapsed" desc="Go to single item image view">
    public static void goToSingleView(View view, Context context, String category, String mode) {
        TextView uidView, itemImageView, itemTitleView, itemDescView, itemPriceView, itemIdView, tagsView, categView;

        uidView = view.findViewById(R.id.postUserId);
        itemIdView = view.findViewById(R.id.itemId);
        itemImageView = view.findViewById(R.id.itemImageUrl);
        itemTitleView = view.findViewById(R.id.itemTitle);
        itemDescView = view.findViewById(R.id.itemDescription);
        itemPriceView = view.findViewById(R.id.itemPrice);
        tagsView = view.findViewById(R.id.tagsView);

        final String userId, itemTitle, imageUrl, itemDesc, itemPrice, itemId, itemTags;

        userId = uidView.getText().toString();
        imageUrl = itemImageView.getText().toString();
        itemTitle = itemTitleView.getText().toString();
        itemDesc = itemDescView.getText().toString();
        itemPrice = itemPriceView.getText().toString();
        itemId = itemIdView.getText().toString();
        itemTags = tagsView.getText().toString();

        new ParticleSystem((Activity) context, 100, R.drawable.fireworksanim, 4000)
                .setSpeedRange(0.1f, 0.25f)
                .setRotationSpeedRange(90, 180)
                .setInitialRotationRange(0, 360)
                .oneShot(view.findViewById(R.id.emiter), 100);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, SingLeItemActivity.class);
                intent.putExtra(Constants.POSTER_ID, userId);
                intent.putExtra(Constants.ITEM_IMAGE, imageUrl);
                intent.putExtra(Constants.ITEM_TITLE, itemTitle);
                intent.putExtra(Constants.ITEM_DESCRIPTION, itemDesc);
                intent.putExtra(Constants.ITEM_PRICE, itemPrice);
                intent.putExtra(Constants.ITEM_ID, itemId);
                intent.putExtra(Constants.ITEM_CATEGORY, category);
                intent.putExtra(Constants.ITEM_TAG, itemTags);
                intent.putExtra(Constants.MODE, mode);
                context.startActivity(intent);
                ((AppCompatActivity) context).finish();


            }
        }, 4000);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Generate an md5 string item id from random uuid">
    public static String generateItemId() {
        return generateMD5Hash(UUID.randomUUID().toString());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Generates 32 bits hash string">
    public static String generateMD5Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHex(bytes, true).toLowerCase(Locale.getDefault());
        } catch (NoSuchAlgorithmException e) {
            return input.split("-")[1];
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Splits a tag string into tag components">
    public static List<String> getTagsList(String tagsString) {
        return new ArrayList<>(Arrays.asList(tagsString.split("__")));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Returns a list of all the images as a local path string">
    public static List<String> getExtraImagesUri(String urisString) {
        return new ArrayList<>(Arrays.asList(urisString.split("__")));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Separates the online urls from the local url and returns the online part">
    public static String getExtraImagesString(String uri) {
        return uri.split("___")[1];
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Check whether a string contains multiple image urls">
    public static boolean hasMultiImage(String url) {
        String end = url.split("___")[1];

        return end.contains("__");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Display all the item images in a dialog">
    public static void showMultipleImages(List<String> images, Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.multi_image_dialog, null, false);
        builder.setView(dialogView);
        LinearLayout rootLayout = dialogView.findViewById(R.id.linear);
        PullInLoader loader = dialogView.findViewById(R.id.dotLoader);

        int i = 1;
        for (String s : images) {
            String url = Urls.BASE_URL + s;
            Timber.e(url);
            @SuppressLint("InflateParams") View multiItem = LayoutInflater.from(context).inflate(R.layout.multi_image_item, null, false);
            ImageView itemView = multiItem.findViewById(R.id.multiItem);
            Glide.with(context).load(url).into(itemView);
            rootLayout.addView(itemView);
            itemView.requestLayout();
            if (i == images.size()) {
                loader.setVisibility(View.GONE);
                break;
            }
            i++;
        }


        dialog = builder.create();
        dialog.show();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Fetch local items by Id">
    public void fetchLocalItemsById(Context context, String userId, FetchCallBacks callBacks) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseManager manager = new DatabaseManager(context).open();
                List<Item> itemList = manager.getMyItems(userId);
                manager.close();
                callBacks.fetchComplete(itemList);
            }
        });
        thread.start();

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Fetch favorites from local db">
    public void fetchFavoritesFromDb(Context context, FetchCallBacks callBacks) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> itemIds = getLastFavoritesList();
                Timber.e(String.valueOf(itemIds.size()));
                if (itemIds.isEmpty()){
                    ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callBacks.fetchError("No favorites yet");

                        }
                    });
                 return;
                }
                DatabaseManager manager = new DatabaseManager(context).open();
                List<Item> items = new ArrayList<>();

                for (String s : itemIds) {
                    manager.getFavourites(s, new CompleteCallBacks() {
                        @Override
                        public void onComplete(Object... objects) {
                            Item item = (Item) objects[0];
                            Timber.e(item.getItemImage());
                            items.add(item);
                            count++;
                            if (count == itemIds.size()) {
                                ( (AppCompatActivity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callBacks.fetchComplete(items);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
        thread.start();


    }
    //</editor-fold>

}
