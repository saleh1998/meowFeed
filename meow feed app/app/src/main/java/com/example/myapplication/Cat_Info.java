package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Cat_Info extends AppCompatActivity {
    ImageButton backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cat_info);;
        backbtn = findViewById(R.id.info_btnBack);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView youtubeLinkTextView = findViewById(R.id.youtubeLinkTextView);
        youtubeLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the YouTube video link in a browser or YouTube app
                String youtubeVideoUrl = "https://youtu.be/wPrOCXGCMpA?feature=shared";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeVideoUrl));
                startActivity(intent);
            }
        });
    }

}