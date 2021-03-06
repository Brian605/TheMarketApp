package com.returno.tradeit.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.BuildConfig;
import com.returno.tradeit.R;
import com.returno.tradeit.utils.Commons;
import com.returno.tradeit.utils.Constants;
import com.returno.tradeit.utils.Tagger;

import timber.log.Timber;
public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText UserNameText,UserMailText,PassWord1Text,Password2Text,PhoneText;
    private String UserName,UserEmail,Password1,Password2,UserPhone,UserId;
    private ValueEventListener userListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    //getSupportActionBar().hide();
    if (BuildConfig.DEBUG){
        Timber.plant(new Timber.DebugTree());
    }

UserNameText=findViewById(R.id.RegisterName);
UserMailText=findViewById(R.id.RegisterEmail);
PassWord1Text=findViewById(R.id.RegisterPassword);
Password2Text=findViewById(R.id.RegisterPasswordConfirm);
PhoneText=findViewById(R.id.RegisterPhone);

        MaterialButton login = findViewById(R.id.RegisterLogin);
        MaterialButton register = findViewById(R.id.RegisterRegister);

login.setOnClickListener(view -> {
    Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
    startActivity(intent);
});

register.setOnClickListener(view -> {
    UserName=UserNameText.getText().toString().trim();
    UserEmail=UserMailText.getText().toString().trim();
    Password1=PassWord1Text.getText().toString().trim();
    Password2=Password2Text.getText().toString().trim();
    UserPhone=PhoneText.getText().toString().trim();


if(!Commons.getInstance().isValidUserName(UserName)){
UserNameText.setError("Wrong Username Format Use 4-6 characters with both lower and capital letters");
Tagger.forceFocus(UserNameText);
return;

}
if(!Commons.getInstance().isValidEmail(UserEmail)){
    UserMailText.setError("Wrong email format");
    Tagger.forceFocus(UserMailText);
return;
}
  if (!isValidPass(Password1,Password2)){
      PassWord1Text.setError("Use more than 8 characters for password");
      Tagger.forceFocus(PassWord1Text);
      return;

  }
      if (!isValidPhone(UserPhone) && !TextUtils.isDigitsOnly(UserPhone)){
          PhoneText.setError("The phone number must begin with a 0 and contain only 10 digits");
          Tagger.forceFocus(PhoneText);
          return;

      }
          registerUser(UserName,UserEmail,UserPhone,Password1);


});
    }




    private boolean isValidPass(String password1, String password2){
        return password1.equals(password2) && password1.length() > 8;
    }

    private boolean isValidPhone(String s){
        //Scanner scanner=new Scanner(s);
        return s.charAt(0)=='0' && s.length()==10 && TextUtils.isDigitsOnly(s) ;
    }

    private void registerUser(final String name, final String email, final String phone, final String password){

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(R.layout.progressdialog);
        final Dialog dialog=builder.create();
        dialog.show();

        final FirebaseAuth auth=FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
           auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(authResult -> saveUser(authResult, name)).addOnFailureListener(e -> {
               dialog.dismiss();
               Toast.makeText(getApplicationContext(), "Could Not Register :" + e.getMessage(), Toast.LENGTH_LONG).show();

           });


            //UserId=FirebaseAuth.getInstance().getCurrentUser().getUid();


        }).addOnSuccessListener(authResult -> {
            setResult(Activity.RESULT_OK);
            dialog.dismiss();
            Toast.makeText(RegisterActivity.this,"Registration Successful",Toast.LENGTH_LONG) .show();
            Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(intent);
        }).addOnFailureListener(e -> {
            Toast.makeText(RegisterActivity.this,"Cannot Register "+ e.getMessage(),Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }).addOnCanceledListener(() -> {

        });
    }

    private void saveUser(AuthResult authResult, String name) {
        UserId= authResult.getUser().getUid();
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference reference=database.getReference(Constants.FIREBASE_USERS_DIR).child(UserId);

        userListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child(Constants.USER_NAME).setValue(name) ;
                reference.child(Constants.USER_PHONE).setValue(UserPhone);
                reference.child(Constants.USER_ID).setValue(UserId);

                reference.removeEventListener(userListener);
                //Timber.e("Generated user Id %s", userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addListenerForSingleValueEvent(userListener);
    }
}
