package com.example.nanthu.homeui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

  private  Button btn, btnlog;
  private  Button btns;
  private  EditText UserEmail,UserPass;
  private FirebaseAuth mAuth;
  private ProgressDialog loadingBar;
  private Boolean mailchecker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UserEmail=(EditText)findViewById(R.id.userName);
        UserPass=(EditText)findViewById(R.id.password);
        loadingBar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btns=(Button)findViewById(R.id.btnRegister_here);

        btns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
        btnlog=(Button)findViewById(R.id.btn_login);//login button

        btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowingUserToLogin();
            }
        });

        btn=(Button)findViewById(R.id.btnForgot_Password);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this, Reset_passwordActivity.class);
                startActivity(i);
            }
        });
        // toolbar();
    }

    private void AllowingUserToLogin() {
        String email=UserEmail.getText().toString();
        String password=UserPass.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Enter the mail Address...",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Enter the Password...",Toast.LENGTH_SHORT).show();

        }else{
            loadingBar.setTitle("Login");
            loadingBar.setMessage("please wait, while we are allowing you to login into your  Account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        verifyMailId();
                        loadingBar.dismiss();

                    }else{
                        String message=task.getException().getMessage();
                        Toast.makeText(LoginActivity.this,"error :"+message,Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    }
                }
            });
        }
    }

    private void SendUserToMainActivity() {
        Intent intent=new Intent(this,Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void verifyMailId(){
        FirebaseUser user=mAuth.getCurrentUser();
        mailchecker=user.isEmailVerified();

        if(mailchecker){
            SendUserToMainActivity();
            Toast.makeText(LoginActivity.this,"You Logged In SuccessFully",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(LoginActivity.this,"please verify your account first",Toast.LENGTH_SHORT).show();
            mAuth.signOut();

        }
    }

    private void SendUserToMainActivity2() {
        Intent intent = new Intent(this, ProfileSetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            SendUserToMainActivity();
        }
    }


    /*
    public void toolbar(){
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Login");

        //actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }
*/

}
