package com.example.nanthu.homeui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {
    RatingBar mRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mRatingBar=(RatingBar)findViewById(R.id.rating_bar);
        toolbar();
    }

    public void toolbar(){
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(R.string.feedback);

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        Intent i = new Intent(FeedbackActivity.this, Home.class);
        startActivity(i);
        finish();
        Toast.makeText(this,"Home",Toast.LENGTH_LONG).show();

        return super.onOptionsItemSelected(item);
    }
}