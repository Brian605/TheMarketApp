package com.returno.tradeit.utils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.returno.tradeit.callbacks.CompleteCallBacks;
import com.returno.tradeit.callbacks.CounterCallBacks;
import com.returno.tradeit.callbacks.DeleteCallBacks;
import com.returno.tradeit.callbacks.FetchCallBacks;
import com.returno.tradeit.callbacks.UploadCallBacks;
import com.returno.tradeit.models.Item;
import com.returno.tradeit.models.Notification;
import com.returno.tradeit.models.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class UploadUtils {

    //<editor-fold defaultstate="collapsed" desc="Upload an item">
    public void uploadItem(Item item, UploadCallBacks callBacks){
        AndroidNetworking.upload(Urls.ITEM_POST_URL)
                .addMultipartFileList("file[]",getArrayFiles(item.getItemImage()))
                .addMultipartParameter(Constants.ITEM_ID,item.getItemId())
                .addMultipartParameter(Constants.ITEM_CATEGORY,item.getItemCategory())
                .addMultipartParameter(Constants.ITEM_TITLE,item.getItemName())
                .addMultipartParameter(Constants.ITEM_DESCRIPTION,item.getItemDescription())
                .addMultipartParameter(Constants.ITEM_PRICE, String.valueOf(item.getItemPrice()))
                .addMultipartParameter(Constants.ITEM_TAG,item.getItemTag())
                .addMultipartParameter(Constants.USER_ID,item.getItemPosterId())
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener((bytesUploaded, totalBytes) -> {
                    double percentage=(double)((bytesUploaded/totalBytes)*100);
                    callBacks.onProgressUpdated((int)percentage);
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Timber.e(response.toString());
                        try {
                           /* if (response.has("upload") && response.has("post") && response.getString("upload").equals("success")
                             && response.getString("post").equals("success")){

                */
                            Notification notification=new Notification("New Item Posted",item.getItemName()+" Ksh."+item.getItemPrice(),item.getItemCategory());
                            new FirebaseUtils().postAPushNotification(notification,item.getItemId());

                            callBacks.onUploadSuccess();
                        } catch (Exception e) {
                            onError(new ANError(e));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
Timber.e(anError.getMessage());
callBacks.onError(anError.getMessage());
                    }
                });


    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get a list of the Images to Upload">
    public static List<File> getArrayFiles(String fileString){
        Timber.e(fileString);
       List<File> filesStringMap=new ArrayList<>();

       for (String s:fileString.split("___")){
           filesStringMap.add(new File(s));
       }
       return filesStringMap;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get the number of category items online">
    public void getOnlineItemsCount(String category, CounterCallBacks counterCallBacks){
        Thread thread=new Thread(() -> AndroidNetworking.post(Urls.ITEM_COUNT_URL)
                .addBodyParameter(Constants.ITEM_CATEGORY,category)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);
                        try {
                            if (Integer.parseInt(response)>0){
                                counterCallBacks.counterResult(Integer.parseInt(response));
                            }else {
                                counterCallBacks.noData();
                            }
                        }catch (NumberFormatException e){
                            counterCallBacks.onError(e.getMessage());
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        counterCallBacks.onError(anError.getMessage());
                    }
                }));
        thread.start();

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Gets all items in a certain category">
    public void fetchItems(String category, int limit, FetchCallBacks fetchCallBacks){
        AndroidNetworking.post(Urls.ITEM_FETCH_URL)
                .addBodyParameter(Constants.ITEM_CATEGORY,category)
                .addBodyParameter(Constants.FETCH_LIMIT, String.valueOf(limit))
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Timber.e(response.toString());
try {
    List<Item>items=new ArrayList<>();
    for (int i=0;i<response.length();i++){
        JSONObject object=response.getJSONObject(i);
     String itemId=object.getString(Constants.ITEM_ID) ;
     String itemName=object.getString(Constants.ITEM_TITLE);
     String description=object.getString(Constants.ITEM_DESCRIPTION);
     String itemPrice=object.getString(Constants.ITEM_PRICE);
     String itemCategory=object.getString(Constants.ITEM_CATEGORY);
     String itemTags=object.getString(Constants.ITEM_TAG);
     String itemImages=object.getString(Constants.ITEM_IMAGE);
     String posterId=object.getString(Constants.USER_ID);

     items.add(new Item(itemId,itemName,description,Integer.parseInt(itemPrice),itemTags,itemImages,posterId,itemCategory));
    }

    fetchCallBacks.fetchComplete(items);

}catch (JSONException e){
onError(new ANError(e));
}

                    }

                    @Override
                    public void onError(ANError anError) {
fetchCallBacks.fetchError(anError.getMessage());
                    }
                });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Gets all items in a certain category">
    public void fetchItemsById(String itemId, FetchCallBacks fetchCallBacks){
        AndroidNetworking.post(Urls.SINGLE_ITEM_FETCH_URL)
                .addBodyParameter(Constants.ITEM_ID,itemId)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Timber.e(response.toString());
                        try {
                            List<Item>items=new ArrayList<>();
                            for (int i=0;i<response.length();i++){
                                JSONObject object=response.getJSONObject(i);
                                String itemId=object.getString(Constants.ITEM_ID) ;
                                String itemName=object.getString(Constants.ITEM_TITLE);
                                String description=object.getString(Constants.ITEM_DESCRIPTION);
                                String itemPrice=object.getString(Constants.ITEM_PRICE);
                                String itemCategory=object.getString(Constants.ITEM_CATEGORY);
                                String itemTags=object.getString(Constants.ITEM_TAG);
                                String itemImages=object.getString(Constants.ITEM_IMAGE);
                                String posterId=object.getString(Constants.USER_ID);

                                items.add(new Item(itemId,itemName,description,Integer.parseInt(itemPrice),itemTags,itemImages,posterId,itemCategory));
                            }

                            fetchCallBacks.fetchComplete(items);

                        }catch (JSONException e){
                            onError(new ANError(e));
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        fetchCallBacks.fetchError(anError.getMessage());
                    }
                });
    }
    //</editor-fold>


/*


    //<editor-fold defaultstate="collapsed" desc="Unsafe OkHttpClient to force insecure connections">
    public static OkHttpClient getUnsafeClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null,trustAllCerts,new SecureRandom());

            final SSLSocketFactory sslSocketFactory=sslContext.getSocketFactory();
            OkHttpClient.Builder builder=new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory,(X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
            builder.addNetworkInterceptor(new StethoInterceptor());

            return builder.build();
        }catch (NoSuchAlgorithmException | KeyManagementException e){
            throw new RuntimeException(e);
        }
    }
    //</editor-fold>
*/

    //<editor-fold defaultstate="collapsed" desc="Delete an item from the online database as well as its images">
    public void deleteItem(String image, String itemId, DeleteCallBacks listener){
        AndroidNetworking.post(Urls.ITEM_DELETE_URL)
                .addBodyParameter(Constants.ITEM_ID,itemId)
                .addBodyParameter(Constants.ITEM_IMAGE,image)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);
                        if (response.equals("Deleted")){
                            listener.onDelete();
                            return;
                        }
                        onError(new ANError(response));
                    }

                    @Override
                    public void onError(ANError anError) {
                        listener.onError(anError.getMessage());
                    }
                });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get requests from database">
    public void fetchRequests(CompleteCallBacks listener){
        Thread thread=new Thread(() -> AndroidNetworking.post(Urls.FETCH_REQUEST_URL)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
try{
List<Request>requests=new ArrayList<>();
for (int i=0;i<response.length();i++) {
JSONObject object = response.getJSONObject(i);
String userId=object.getString(Constants.USER_ID);
String userName=object.getString(Constants.USER_NAME);
String userPhone=object.getString(Constants.USER_PHONE);
String requestItem=object.getString(Constants.ITEM_TITLE);
String requestId=object.getString(Constants.ITEM_ID);
requests.add(new Request(userName,userPhone,requestItem,userId,requestId));
}
listener.onComplete(requests);
}catch (JSONException e){
onError(new ANError(e.getMessage()));
}
                    }

                    @Override
                    public void onError(ANError anError) {
listener.onFailure(anError.getMessage());
                    }
                }));
        thread.start();

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Post an item request to the database">
    public void postRequest(Request request,UploadCallBacks callBacks){
        Thread thread=new Thread(() -> AndroidNetworking.post(Urls.POST_REQUEST_URL)
                .addBodyParameter(Constants.ITEM_ID,request.getRequestId())
                .addBodyParameter(Constants.ITEM_TITLE,request.getRequestItem())
                .addBodyParameter(Constants.USER_NAME,request.getUserName())
                .addBodyParameter(Constants.USER_ID,request.getUserId())
                .addBodyParameter(Constants.USER_PHONE,request.getUserPhone())
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")){
                            callBacks.onUploadSuccess();
                            String body;
                            if (request.getRequestItem().length()>16){
                                body=request.getRequestItem().substring(0,17);
                            }else {
                                body=request.getRequestItem();
                            }
                            Notification notification=new Notification("Item Requests",body,"requests");
                            new FirebaseUtils().postAPushNotification(notification,request.getRequestId());
                        }else {
                            onError(new ANError(response));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
callBacks.onError(anError.getMessage());
                    }
                }));
        thread.start();

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Delete a Request from database">
    public synchronized void deleteRequest(String id, DeleteCallBacks listener){
        new Thread(() -> AndroidNetworking.post(Urls.DELETE_REQUEST_URL)
                .addBodyParameter(Constants.ITEM_ID,id)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Deleted")){
                            listener.onDelete();
                            return;
                        }
                        if (response.isEmpty()){
                            response="An error occurred while processing";
                        }
                        onError(new ANError(response));

                    }

                    @Override
                    public void onError(ANError anError) {
listener.onError(anError.getLocalizedMessage());
                    }
                })).start();
    }
    //</editor-fold>
}
