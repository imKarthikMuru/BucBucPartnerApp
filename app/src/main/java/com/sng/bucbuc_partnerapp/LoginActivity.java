package com.sng.bucbuc_partnerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.ETC1;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText EMailET,PasswordET,BSEMailET;
    Button LoginBTN,BSRestBtn;
    TextView ForgotPasswordTv;

    LoadingView loadingProgress;
    String email,password,uid;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewByID();

    }



    private void RequestResetMail() {

        String ResetMail=BSEMailET.getText().toString();

        if (ResetMail.isEmpty()){
            BSEMailET.setError("Required.");
        }else if(!Patterns.EMAIL_ADDRESS.matcher(ResetMail).matches()){
            BSEMailET.setError("Incorrect Email");
        }else {

            loadingProgress.ShowProgress(this,"Requesting for Reset Mail.",false);

            FirebaseAuth.getInstance().sendPasswordResetEmail(ResetMail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                    }
                    bottomSheetDialog.dismiss();
                    loadingProgress.hideProgress();
                }
            });
        }

    }

    private void DoLogin() {
        email=EMailET.getText().toString();
        password=PasswordET.getText().toString();

        if (email.isEmpty()){
            EMailET.setError("Required.");
        }else if(password.isEmpty()){
            PasswordET.setError("Required.");
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            EMailET.setError("Incorrect Email");
        }else if (password.length()<5){
            PasswordET.setError("Password must be 5 characters");
        }else {

            loadingProgress.ShowProgress(this,"Logging in",false);

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Error! :"+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                    loadingProgress.hideProgress();
                }
            });

        }
    }

    private void findViewByID() {

        EMailET=(EditText)findViewById(R.id.mail);
        PasswordET=(EditText)findViewById(R.id.password);
        LoginBTN=(Button)findViewById(R.id.loginBtn);
        ForgotPasswordTv=(TextView)findViewById(R.id.forgotPasswordTv);

        loadingProgress=LoadingView.getInstance();

        //Bottomsheet dialog for Order Summary
        bottomSheetDialog = new BottomSheetDialog(LoginActivity.this);
        bottomSheetDialog.setContentView(R.layout.bottomsheet_password_reset);
        bottomSheetDialog.setCancelable(true);

        BSEMailET=bottomSheetDialog.findViewById(R.id.Resetemail);
        BSRestBtn=bottomSheetDialog.findViewById(R.id.resetBtn);

        ForgotPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });


        BSRestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestResetMail();
            }
        });


        LoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoLogin();
            }
        });
    }
}