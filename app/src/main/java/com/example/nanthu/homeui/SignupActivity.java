package com.example.nanthu.homeui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private EditText mUsername,mUserEmail,mUserPhoneNum,mUserpass,mUserConformPass;
    private Button mRegister;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

      //  mUsername=(EditText)findViewById(R.id.userName);
        mUserEmail=(EditText)findViewById(R.id.email);
        mUserPhoneNum=(EditText)findViewById(R.id.phone);
        mUserpass=(EditText)findViewById(R.id.password);
        mUserConformPass=(EditText)findViewById(R.id.confirm_password);
        loadingBar=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();

        mRegister=(Button)findViewById(R.id.btn_signup);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatenewAccount();
            }
        });

        toolbar();
    }

    private void CreatenewAccount() {
        //String Name=mUsername.getText().toString();
        String Email=mUserEmail.getText().toString();
        String phoneNum=mUserPhoneNum.getText().toString();
        String password=mUserpass.getText().toString();
        String confirmpass=mUserConformPass.getText().toString();

       // if(TextUtils.isEmpty(Name)){
           // Toast.makeText(this,"Please enter the Nmae",Toast.LENGTH_SHORT).show();
          if(TextUtils.isEmpty(Email)){
            Toast.makeText(this,"Please enter the email address",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(phoneNum)||phoneNum.length()!=10){
            Toast.makeText(this,"Please enter the Phone number ",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter the Password ",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(confirmpass)){
            Toast.makeText(this,"Please enter the Confirm Password ",Toast.LENGTH_SHORT).show();
        }else if(!password.equals(confirmpass)){
            Toast.makeText(this,"Do not match password and confirm password... ",Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("please wait, while we are creating your new Account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

        mAuth.createUserWithEmailAndPassword(Email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    SendVerifycationEmailaddress();
                    loadingBar.dismiss();
                }else{
                    String message=task.getException().getMessage();
                    Toast.makeText(SignupActivity.this,"Error "+message,Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                }
            }
        });
        }
    }







    private void SendVerifycationEmailaddress(){
        FirebaseUser user=mAuth.getCurrentUser();

        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SignupActivity.this,"Registration Successfuly,we 've sent you mail id .please check and verify you are account",Toast.LENGTH_LONG).show();

                        SendUserToMainActivity2();
                       // mAuth.signOut();
                    }
                    else{
                        String message=task.getException().getMessage();
                        Toast.makeText(SignupActivity.this,"Error "+message,Toast.LENGTH_SHORT).show();
                        mAuth.signOut();


                    }
                }
            });
        }
    }



    private void sendLoginActivity() {
        Intent intent=new Intent(this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void SendUserToMainActivity2() {
        Intent intent=new Intent(this,ProfileSetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void toolbar(){
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Sign Up");

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }
}
