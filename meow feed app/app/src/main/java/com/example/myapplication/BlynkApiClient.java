package com.example.myapplication;

import android.os.AsyncTask;

import okhttp3.*;

public class BlynkApiClient {
    private static final String BLYNK_API_BASE_URL = "https://blynk.cloud/external/api/update?token=";
    private static final String AUTH_TOKEN = "C2lV2IP_li3-CE9gHoRBjDg0O8W8XIOb";
    private static final String PIN_V4 = "V4"; // Replace with the appropriate pin
    private OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public BlynkApiClient() {
        client = new OkHttpClient();
    }

    public void sendCommand() {
//        String url = "https://blynk.cloud/external/api/update?token=Q4gRQRIb5TKNkrUwEY89OTzOzTjoDqwK&V4=1";
        String url = BLYNK_API_BASE_URL + AUTH_TOKEN + "&" + PIN_V4 +"=1";

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    Response response = client.newCall(request).execute();
                    // Handle the response if necessary
                    if(response.isSuccessful()){
                        System.out.println("Request successful: " + response.body().string());
                    } else {
                        System.out.println("Request not successful: " + response.body().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
    public void sendCommand2() {
//        String url = "https://blynk.cloud/external/api/update?token=Q4gRQRIb5TKNkrUwEY89OTzOzTjoDqwK&V4=1";
        String url = BLYNK_API_BASE_URL + AUTH_TOKEN + "&" + PIN_V4 +"=0";

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    Response response = client.newCall(request).execute();
                    // Handle the response if necessary
                    if(response.isSuccessful()){
                        System.out.println("Request successful: " + response.body().string());
                    } else {
                        System.out.println("Request not successful: " + response.body().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }


}



