package com.example.nanthu.homeui;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Reset_passwordActivity extends AppCompatActivity {

    private Button resetButton;
    private EditText resetmailInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        resetButton=(Button)findViewById(R.id.resetbtn);
        resetmailInput=(EditText)findViewById(R.id.forgetmailaddress);
        mAuth=FirebaseAuth.getInstance();

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String Mymail=resetmailInput.getText().toString();
                if(TextUtils.isEmpty(Mymail)){
                    Toast.makeText(Reset_passwordActivity.this,"Please write validate mail first..",Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.sendPasswordResetEmail(Mymail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Reset_passwordActivity.this,"Please Check your Mail Account..if you want reset the password ..",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Reset_passwordActivity.this,LoginActivity.class));
                            }else{
                                String message=task.getException().getMessage();
                                Toast.makeText(Reset_passwordActivity.this,"Error:occured "+message,Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });

        toolbar();
    }

    public void toolbar(){
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Reset password");

        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        Intent i = new Intent(Reset_passwordActivity.this, LoginActivity.class);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }
}

