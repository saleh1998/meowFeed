//package com.example.myapplication;
//
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.exoplayer2.DefaultDataSourceFactory;
//import com.google.android.exoplayer2.Format;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.source.MediaSource;
//import com.google.android.exoplayer2.upstream.DataSpec;
//import com.google.android.exoplayer2.upstream.DataSource;
//import com.google.android.exoplayer2.upstream.*;
//import com.google.android.exoplayer2.util.Util;
//import com.google.android.exoplayer2.MediaItem;
//import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
//import com.google.android.exoplayer2.source.hls.HlsSingleSampleMediaSource;
//import com.google.android.exoplayer2.C;
//import com.google.android.exoplayer2.ui.PlayerView;
//
//public class VideoPlayerActivity extends AppCompatActivity {
//
//    private PlayerView playerView;
//    private SimpleExoPlayer player;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_video_player);
//
//        playerView = findViewById(R.id.videoView);
//
//        // Create a new ExoPlayer instance
//        player = new SimpleExoPlayer.Builder(this).build();
//        playerView.setPlayer(player);
//
//        // Set the data source for the ExoPlayer
//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
//                Util.getUserAgent(this, "YourApp"));
//        Uri videoUri = Uri.parse("https://cat-feeder-bacf1-default-rtdb.europe-west1.firebasedatabase.app/videoFrames.json");
//
//        // Prepare the video source and start playing
//        prepareVideoSourceAndPlay();
//        // Here, you should handle fetching data from Firebase Realtime Database and converting it into a video stream
//        // Use your preferred method to fetch data and convert it into a video stream (e.g., Base64 decoding)
//        // Pass the converted video stream to createMediaSource method
//         MediaSource mediaSource = createMediaSource(yourVideoStream);
//        player.prepare(mediaSource);
//    }
//
//    private void prepareVideoSourceAndPlay() {
//        // Fetch frames from Firebase Realtime Database in Base64 format (dummy data for example)
//        String[] base64Frames = fetchFramesFromFirebase(); // Implement this method
//
//        // Convert Base64 frames to MediaSource objects
//        MediaSource[] mediaSources = new MediaSource[base64Frames.length];
//        for (int i = 0; i < base64Frames.length; i++) {
//            // ... (rest of your code)
//        }
//
//        // Create a ConcatenatingMediaSource to concatenate the frames into a video stream
//        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource(false, false, mediaSources);
//
//        // Set the prepared media source to the ExoPlayer
//        player.setMediaSource(concatenatingMediaSource);
//
//        // Prepare the player and start playing
//        player.prepare();
//        player.setPlayWhenReady(true);
//    }
//
//    // Dummy method to simulate fetching frames from Firebase Realtime Database
//    private String[] fetchFramesFromFirebase() {
//        // Implement logic to fetch frames from Firebase and return as Base64 strings
//        return new String[] {
//                "BASE64_FRAME_1",
//                "BASE64_FRAME_2",
//                // ... Add more frames as needed
//        };
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        player.release();
//    }
//}
