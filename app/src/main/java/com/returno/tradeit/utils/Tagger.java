package com.returno.tradeit.utils;

import android.view.View;

import com.returno.tradeit.R;

import java.util.Random;

public class Tagger {

   public static int currentId;
   public int getTagBackground(){

       Random random=new Random();
       int id=random.nextInt(4);

       if (id==0){
           return R.drawable.tag1;
       }else
        if (id==1){
            return R.drawable.tag2;
        }
      else
        if (id==2){
            return R.drawable.tag3;
        }
      else
        if (id==3){
            return R.drawable.tag4;

        }else {
            return R.drawable.tag5;
        }

   }

   public int getAvatarColor(){
       Random random=new Random();
       int id=random.nextInt(4);

       switch (id){
           case 0:return R.color.login_header;
           case 1:return R.color.colorPrimary;
           case 2:return R.color.colorPrimaryDark;
           case 3:return R.color.colorAccent;
           case 4:return R.color.purple;
           default:return R.color.card_view_dark;
       }
   }
   public float getRating(float total){
       if (total<10){
           return 1f;
       }else
       if (total<50){
           return 2f;
       }else
       if (total<100){
           return 3f;
       }else
       if (total<150){
           return 4f;
       }else
       return 5f;
   }

public static void forceFocus(View view){
       view.requestFocus();
}

public static String getInternationalNumber(String number){
   // PhoneNumberUtil.PhoneNumberFormat format=PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL;

    if (number.startsWith("07") && number.length()==10){
        number=number.replaceFirst("07","+2547");
        return number;
    }

   if (number.startsWith("254") && number.length()==12){
       number=number.replaceFirst("254","+254");
       return number;
   }

    return number;
}

}
