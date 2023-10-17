package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.longdo.mjpegviewer.MjpegView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

    public class LiveFootage extends AppCompatActivity {
        ImageButton btnBack;
        ImageView livestr;
        private MjpegView videoView;
        private DatabaseReference databaseReference;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_live_footage);
            livestr = findViewById(R.id.livestream);
            videoView =(MjpegView) findViewById(R.id.mjpegview);
            btnBack = findViewById(R.id.LiveStream_btnBack);
            videoView.setMode(MjpegView.MODE_FIT_WIDTH);
            videoView.setAdjustHeight(true);
            videoView.setSupportPinchZoomAndPan(true);
            videoView.setUrl(" https://9e9a-176-106-227-249.ngrok-free.app/mjpeg/1");
            videoView.startStream();
           animateLive();


            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.stopStream();
                    finish();
                }
            });
        }

        private void animateLive() {
            int catImageResourceId = getResources().getIdentifier("live2", "drawable", "com.example.myapplication");
            // Load the random cat image into the ImageView using Glide
            Glide.with(this).asGif().load(catImageResourceId).into(livestr);
        }


    }



