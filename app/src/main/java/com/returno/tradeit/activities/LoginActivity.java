package com.returno.tradeit.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.returno.tradeit.R;
import com.returno.tradeit.local.PreferenceManager;
import com.returno.tradeit.utils.Constants;
import com.returno.tradeit.utils.ItemUtils;
import com.returno.tradeit.utils.Tagger;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private AppCompatEditText EmailEditText, PassWordEditText;
    private String email, password;
    private Dialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialise views
        EmailEditText = findViewById(R.id.LoginUserMail);
        PassWordEditText = findViewById(R.id.LoginPassword);
        Button login = findViewById(R.id.LoginLogin);
        Button register = findViewById(R.id.LoginRegister);
        TextView reset = findViewById(R.id.LoginReset);

        showPermissionInfo();
        //Add event listeners
        login.setOnClickListener(view -> {

            email = EmailEditText.getText().toString().trim();
            password = PassWordEditText.getText().toString().trim();

            if (isValidPassword(password)) {
                PassWordEditText.setError("Field cannot be blank");
                Tagger.forceFocus(PassWordEditText);
                return;
            } else if (isValidEmail(email)) {
                EmailEditText.setError("Invalid email format");
                Tagger.forceFocus(EmailEditText);
                return;
            }

            LoginUser(email, password);

        });

        //Register
        register.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        reset.setClickable(true);
        reset.setFocusable(true);

        //Reset Password
        reset.setOnClickListener(view -> {

            LayoutInflater inflater=LayoutInflater.from(LoginActivity.this);
            View view1=inflater.inflate(R.layout.password_reset,null,false);
            final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setView(view1);
            dialog=builder.create();
            dialog.setCanceledOnTouchOutside(false);

            Button bt = view1.findViewById(R.id.ResetReset);
            Button cancel = view1.findViewById(R.id.ResetCancel);
            final AppCompatEditText Em = view1.findViewById(R.id.ResetEmail);

            assert bt != null;
            bt.setOnClickListener(view22 -> {

                assert Em != null;
                String email = Em.getText().toString().trim();
                if (isValidEmail(email)) {
                    Em.setError("This email format is invalid");
                    return;
                }
                resetUserPass(email);


            });

            //assert cancel != null;
            cancel.setOnClickListener(view2 -> {
                dialog.cancel();
                dialog.dismiss();
            });
            dialog.show();
        });

    }

    private void showPermissionInfo() {
        if (!PreferenceManager.getInstance().isBooleanValueTrue(Constants.IS_LOGIN_FIRST_LAUNCH,getApplicationContext())){
           requestPermissions();
            return;
        }
        LayoutInflater inflater=LayoutInflater.from(LoginActivity.this);
        View view1=inflater.inflate(R.layout.permission_information_dialog,null,false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(view1);
        dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        MaterialButton bt = view1.findViewById(R.id.ok);
        bt.setOnClickListener(v -> {
            if (dialog.isShowing())dialog.dismiss();
            PreferenceManager.getInstance().storeBooleanValue(Constants.IS_LOGIN_FIRST_LAUNCH,true,getApplicationContext());
            requestPermissions();
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
dialog.show();


    }

    private void requestPermissions() {



        Dexter.withActivity(LoginActivity.this)
                .withPermissions(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()){
                            new ItemUtils().showMessageDialog(getApplicationContext(), StringUtils.join(report.getDeniedPermissionResponses()));
                        finishAffinity();}
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
token.continuePermissionRequest();
                    }
                }).check();
    }

    private boolean isValidPassword(String s) {

        return s == null;
    }

    private boolean isValidEmail(String s) {
        String regex = "^.+@.+\\..+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        return !matcher.matches();


    }

    private void LoginUser(String userName, String Pass) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progressdialog);
         dialog = builder.create();
        dialog.show();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(userName, Pass).addOnCompleteListener(task -> {
           // Toast.makeText(LoginActivity.this, "Login Complete", Toast.LENGTH_LONG).show();
        }).addOnSuccessListener(authResult -> {

            DatabaseReference reference= FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS_DIR).child(authResult.getUser().getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String  Username=snapshot.child(Constants.USER_NAME).getValue().toString();
                final String phone=snapshot.child(Constants.USER_PHONE).getValue().toString();

                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    setResult(100);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(LoginActivity.this, "Cannot Login " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void resetUserPass(final String s) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(s).addOnSuccessListener(aVoid -> Toast.makeText(LoginActivity.this, "A password reset email was sent to " + s, Toast.LENGTH_LONG).show()).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Failed to generate reset email " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
       // FirebaseApp.initializeApp(LoginActivity.this);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
