package com.example.myapplication;
import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FeedingJobService extends JobService {
    private BlynkApiClient blynkApiClient;
    private OkHttpClient client;
    double currentWeight = 0.0 ,previousWeight;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());


    DB_Manager dbManager = DB_Manager.getInstance(this);
    @Override
    public boolean onStartJob(JobParameters params) {
        // This method is called when the scheduled job should be executed.
        // Perform the feeding operation here.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "FeedingChannelId";
            CharSequence channelName = "Feeding Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        blynkApiClient = new BlynkApiClient();
        client = new OkHttpClient();
        // For demonstration, let's show a toast message indicating that feeding is being done.
        fetchWeightDataWithoutDB();
        try {
            waitFor(2000); // Wait for 2 seconds before sending the first command to ESP32
            waitFor(2000); // Wait for 2 seconds before sending the first command to ESP32
            sendCommandToESP32();
            waitFor(1000); // Wait for 1 second before sending the second command to ESP32
            sendCommand2ToESP32();
            waitFor(4000); // Wait for 2 seconds before fetching weight data again
            fetchWeightData();

            scheduleNotification();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Return true if the job is still running in the background, false if it's done.
        // In this example, the job is completed immediately, so return false.
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // This method is called if the job is prematurely cancelled (e.g., due to constraints not being met).
        // Return true to indicate that the job needs to be rescheduled, or false if it can be dropped.
        // In this example, the job doesn't have to be rescheduled, so return false.
        System.out.println("feeding not happened");
        return false;
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
                    mainHandler.post(new Runnable() {
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


    private void scheduleNotification() {
        String message = "Food is dispensing now!";

        // Create an Intent for the notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Feeding Channel";
            String description = "Channel for Feeding Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("FeedingChannelId", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "FeedingChannelId")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Feeding Notification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Get the NotificationManager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Notify the user
        notificationManager.notify(123, builder.build());
    }
}
