package com.example.nanthu.homeui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Set;

public class SettingActivity extends AppCompatActivity {

    private TextView profilesetting;
    private TextView changePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        profilesetting = findViewById(R.id.textView);
        profilesetting.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(SettingActivity.this,profileActivity.class);
        startActivity(intent);
    }
});

        changePass = findViewById(R.id.textView2);
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,changePasswordActivity.class);
                startActivity(intent);
            }
        });


        toolbar();
    }

    public void toolbar(){
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Setting");

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        Intent i = new Intent(SettingActivity.this, Home.class);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }
}


