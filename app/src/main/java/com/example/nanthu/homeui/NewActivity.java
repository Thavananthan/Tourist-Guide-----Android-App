package com.example.nanthu.homeui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class NewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        ActionBar actionBar=getSupportActionBar();
        TextView mDetailTv=findViewById(R.id.textView);
        ImageView imageView=findViewById(R.id.imageView);

        Intent intent=getIntent();
        String mActionBarTitle=intent.getStringExtra("actionBarTitle");
        String mcontent=intent.getStringExtra("contentTv");
        Bundle bundle=this.getIntent().getExtras();
        int pic=bundle.getInt("image");

        actionBar.setTitle(mActionBarTitle);
        mDetailTv.setText(mcontent);
        imageView.setImageResource(pic);

    }
}
