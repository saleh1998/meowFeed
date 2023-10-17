package com.example.myapplication;

import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import static android.os.SystemClock.sleep;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalDateTime;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;

public class FragmentFeedMeNow extends Fragment {

    Context context;
    Button controlButton;
    private BlynkApiClient blynkApiClient;
    private OkHttpClient client;
    double currentWeight = 0.0 ,previousWeight;
    DB_Manager dbManager = DB_Manager.getInstance(context);
    private ALodingDialog aLodingDialog;
    ImageView animationView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView=inflater.inflate(R.layout.activity_fragment_feed_me_now, container, false);
        controlButton = fragView.findViewById(R.id.mybtn);
        blynkApiClient = new BlynkApiClient();
        client = new OkHttpClient();
        aLodingDialog = new ALodingDialog(context);
        animationView = fragView.findViewById(R.id.animationView);

        ArrayList<OwnerDetails> ownerDetails = new ArrayList<>();
        try {
            ownerDetails = dbManager.getOwnersData();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        OwnerDetails owner = ownerDetails.get(0);
        String catName = owner.getCat();
        controlButton.setText("Feed "+catName);

        animateWaitingCat();
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aLodingDialog.setmyText();
                aLodingDialog.show();
                animateCookingCat();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Move your time-consuming operations here
                        fetchWeightDataWithoutDB();
                        try {
                            waitFor(2000); // Wait for 2 seconds before sending the first command to ESP32
                            waitFor(2000); // Wait for 2 seconds before sending the first command to ESP32
                           sendCommandToESP32();
                            waitFor(1000); // Wait for 1 second before sending the second command to ESP32
                            sendCommand2ToESP32();
                            waitFor(4000); // Wait for 2 seconds before fetching weight data again
                            fetchWeightData();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Update UI on the main thread after background operations are done
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                animateCat();
                                aLodingDialog.cancel();
                                Toast.makeText(context, "food is ready", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });


        return fragView;

    }
    public void setContext(Context context) {
        this.context = context;
    }
    private void sendCommandToESP32() {
        blynkApiClient.sendCommand();
    }

    private void sendCommand2ToESP32() {
        blynkApiClient.sendCommand2();
    }
  /*  private void animateCat() {
        Glide.with(this).asGif().load(R.drawable.cat).into(catImageView);
    }
*/
    private class FetchWeightDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String responseData = null;
            try {
                String apiUrl = "https://blynk.cloud/external/api/get?token=C2lV2IP_li3-CE9gHoRBjDg0O8W8XIOb&V6";
                Request request = new Request.Builder().url(apiUrl).build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    responseData = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseData;
        }

        @Override
        protected void onPostExecute(String responseData)  {
            if (responseData != null) {
                previousWeight = currentWeight;
              //  weightTextView.setText("Weight: " + responseData + " grams");
                currentWeight = Double.parseDouble(responseData);

                // Continue with the rest of your code here
                // Calculate dispensed and total weight values
                double dispensedValue = currentWeight - previousWeight;
                double totalWeightValue = currentWeight;
                Date currentDate = new Date();
                CatData catData = new CatData(dispensedValue, totalWeightValue, currentDate);

                try { dbManager.addData(catData);  }
                catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            else {
                try {
                    throw new myException("not good");
                } catch (myException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void fetchWeightData() {
        new FetchWeightDataTask().execute();
    }


    // The wait method
    public void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    // Custom wait method that handles InterruptedException
    private void waitFor(int milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Set weight data to the TextView
                            previousWeight = currentWeight;
                            //weightTextView.setText("Weight: " + responseData + " grams");
                            currentWeight = Double.parseDouble(responseData);
                        }
                    });
                }
            }
        });
    }

    private void animateCat() {
        // Generate a random number between 0 and 25
        int randomCatNumber = new Random().nextInt(26); // 26 exclusive, so it generates numbers from 0 to 25

        // Construct the resource ID for the randomly chosen cat image
        int catImageResourceId = getResources().getIdentifier("cat" + randomCatNumber, "drawable", "com.example.myapplication");

        // Load the random cat image into the ImageView using Glide
        Glide.with(this).asGif().load(catImageResourceId).into(animationView);
    }
    private void animateCookingCat() {
        // Generate a random number between 0 and 25
        int randomCatNumber = new Random().nextInt(8); // 26 exclusive, so it generates numbers from 0 to 25

        // Construct the resource ID for the randomly chosen cat image
        int catImageResourceId = getResources().getIdentifier("cook" + randomCatNumber, "drawable", "com.example.myapplication");

        // Load the random cat image into the ImageView using Glide
        Glide.with(this).asGif().load(catImageResourceId).into(animationView);
    }
    private void animateWaitingCat() {
        // Generate a random number between 0 and 25
        int randomCatNumber = new Random().nextInt(16); // 26 exclusive, so it generates numbers from 0 to 25

        // Construct the resource ID for the randomly chosen cat image
        int catImageResourceId = getResources().getIdentifier("waiting" + randomCatNumber, "drawable", "com.example.myapplication");

        // Load the random cat image into the ImageView using Glide
        Glide.with(this).asGif().load(catImageResourceId).into(animationView);
    }

}