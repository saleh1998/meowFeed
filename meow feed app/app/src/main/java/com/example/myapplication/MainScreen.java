package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainScreen extends AppCompatActivity {

    private OkHttpClient client;
    ImageView profilePic,liveimg;
    HalfGauge halfGauge;
    Button btnShowData,btnLiveStream,controlButton,infoBtn;
    TextView schedulebtn,greetingTextView;
    ConstraintLayout bgFragmentContainer;
    FragmentFeedMeNow fragmentFeedMeNow = new FragmentFeedMeNow();
    ScheduleFragment scheduleFragment = new ScheduleFragment();
    Fragment currentFrgament = null;
    DB_Manager dbManager;
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        dbManager = DB_Manager.getInstance(this);
        bgFragmentContainer = findViewById(R.id.mainscreen_fragcontainer);
        controlButton = findViewById(R.id.main_screen_feeme);
        schedulebtn = findViewById(R.id.main_screen_schedulebtn);
        btnLiveStream = findViewById(R.id.btnLiveStream);
        profilePic = findViewById(R.id.profile);
        infoBtn = findViewById(R.id.infobtn);
        liveimg = findViewById(R.id.livescale);
        animateLive();




        ArrayList<OwnerDetails> ownerDetails = new ArrayList<>();
        try {
            ownerDetails = dbManager.getOwnersData();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        LocalTime currentTime = LocalTime.now();

        String greeting;
        if (currentTime.isAfter(LocalTime.of(5, 0)) && currentTime.isBefore(LocalTime.of(12, 0))) {
            greeting = "Good Morning";
        } else if (currentTime.isAfter(LocalTime.of(12, 0)) && currentTime.isBefore(LocalTime.of(17, 0))) {
            greeting = "Good Afternoon";
        } else if (currentTime.isAfter(LocalTime.of(17, 0)) && currentTime.isBefore(LocalTime.of(20, 0))) {
            greeting = "Good Evening";
        } else {
            greeting ="Good Night";
        }





        OwnerDetails owner = ownerDetails.get(0);
        String imagePath = owner.getImgPath();
        String ownername = owner.getOwner();

        greetingTextView = findViewById(R.id.greetingTextView); // Replace with your TextView ID
        greetingTextView.setText(greeting+" "+ownername);









        selectedImage = Uri.parse(imagePath);
        profilePic.setImageURI(selectedImage);


        ButtonsClick buttonsClick = new ButtonsClick();
        controlButton.setOnClickListener(buttonsClick);
        schedulebtn.setOnClickListener(buttonsClick);
        btnLiveStream.setOnClickListener(buttonsClick);




        client = new OkHttpClient();
        halfGauge = findViewById(R.id.halfGauge);
        btnShowData = findViewById(R.id.btnShowData);

        // Initialize handler
        Handler handler = new Handler();

        // Create a runnable task to fetch data every 30 seconds
        Runnable fetchDataRunnable = new Runnable() {
            @Override
            public void run() {
                fetchWeightDataWithoutDB();
                // Call the same runnable again after 30 seconds
                handler.postDelayed(this, 10000); // 30000 milliseconds = 30 seconds
            }
        };

        // Start the periodic task
        handler.postDelayed(fetchDataRunnable, 10000); // 30000 milliseconds = 30 seconds



        halfGauge.setMinValue(0.0);
        halfGauge.setMaxValue(20.0);
        Range range1 = new Range();
        range1.setColor(Color.parseColor("#E3E500"));
        range1.setFrom(0.0);
        range1.setTo(10.0);
        Range range2 = new Range();
        range2.setColor(Color.parseColor("#00b20b"));
        range2.setFrom(10.0);
        range2.setTo(20.0);
        halfGauge.addRange(range1);
        halfGauge.addRange(range2);


        btnShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, ShowData.class);
                startActivity(intent); // Start the new activity
            }
        });
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, Cat_Info.class);
                startActivity(intent); // Start the new activity
            }
        });
    }


    class ButtonsClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == controlButton.getId()) {
                greetingTextView.setVisibility(View.INVISIBLE);
                fragmentFeedMeNow.setContext(MainScreen.this);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainscreen_fragcontainer, fragmentFeedMeNow).commit();
                currentFrgament = fragmentFeedMeNow;
            }
            if (view.getId() == schedulebtn.getId()) {
                greetingTextView.setVisibility(View.INVISIBLE);
                scheduleFragment.setContext(MainScreen.this);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainscreen_fragcontainer, scheduleFragment).commit();
                currentFrgament = scheduleFragment;
            }
            if(view.getId() == btnLiveStream.getId()){
                Intent intent = new Intent(MainScreen.this, LiveFootage.class);
                startActivity(intent); // Start the new activity
            }


        }
    }
    private void animateLive() {
        int catImageResourceId = getResources().getIdentifier("online", "drawable", "com.example.myapplication");
        // Load the random cat image into the ImageView using Glide
        Glide.with(this).asGif().load(catImageResourceId).into(liveimg);
    }

    private void fetchWeightDataWithoutDB() {
        String apiUrl = "https://blynk.cloud/external/api/get?token=C2lV2IP_li3-CE9gHoRBjDg0O8W8XIOb&V6"; // Replace YOUR_AUTH_TOKEN with your Blynk auth token
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        // Asynchronous request to fetch weight data
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    // Update UI on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            halfGauge.setValue(Double.parseDouble(responseData));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Create a confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // If user clicks "Yes", exit the app
                        finishAffinity();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // If user clicks "No", dismiss the dialog and do nothing
                        dialog.dismiss();
                    }
                })
                .show();
    }

}