package com.returno.tradeit.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.iceteck.silicompressorr.SiliCompressor;
import com.returno.tradeit.callbacks.CompressionCallBacks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Reducer {

    private static String path=null;
public static void compressImage(Context context, List<String> fileUris, CompressionCallBacks callBacks) {
    List<File>compressedFiles=new ArrayList<>();
    int size=fileUris.size();
    int counter=1;
    Timber.e(String.valueOf(fileUris.size()));

    for (String s:fileUris){
String path= SiliCompressor.with(context)
        .compress(Uri.fromFile(new File(s)).toString(),new File(getCompressDir()));
compressedFiles.add(new File(path));
if (counter==size){
    callBacks.complete(compressedFiles);
    break;
}else {
    counter++;
}



    }

}

    public static String getCompressDir() {
        File dir = new File(Constants.ROOT_DIRECTORY_LOCAL+"Uploads" );

        if (!dir.exists()){
            dir.mkdirs();
        }

        return dir.getAbsolutePath();
    }



    private static String getPathFromUri(Uri imagePath, Context context) {
        Cursor cursor=context.getContentResolver().query(imagePath,null,null,null,null);
        if (cursor==null){
            cursor.close();
            return imagePath.getPath();
        }else {
            cursor.moveToFirst();
            int index=cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            cursor.close();
            return cursor.getString(index);
        }
    }

    }
