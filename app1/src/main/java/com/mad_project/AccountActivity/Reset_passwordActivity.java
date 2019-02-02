package com.mad_project.AccountActivity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.mad_project.MainActivity;
import com.mad_project.R;

public class Reset_passwordActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

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
