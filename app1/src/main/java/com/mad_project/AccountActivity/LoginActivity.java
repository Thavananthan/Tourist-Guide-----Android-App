package com.mad_project.AccountActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.mad_project.MainActivity;
import com.mad_project.R;

public class LoginActivity extends AppCompatActivity {

    Button btn, btnlog;
    Button btns;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btns=(Button)findViewById(R.id.btnRegister_here);

        btns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
        btnlog=(Button)findViewById(R.id.btn_login);

        btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this, changePasswordActivity.class);
                startActivity(i);
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
