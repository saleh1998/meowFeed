package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import okhttp3.OkHttpClient;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private TextView weightTextView;
    private OkHttpClient client;
    private BlynkApiClient blynkApiClient;
    private ImageView catImageView;
    Button controlButton, btnStop;
    double currentWeight = 0.0 ,previousWeight;
    private static volatile boolean flag;
    Button openActivityButton;
    EditText etName,etCatName;
    ImageButton addImage;
    Uri selectedImage;
    DB_Manager dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = DB_Manager.getInstance(this);
        setContentView(R.layout.activity_main);
      if(dbManager.SetupCompleted())
      {
          Intent intent = new Intent(MainActivity.this, MainScreen.class);
          startActivity(intent); // Start the new activity
      }
         openActivityButton = findViewById(R.id.openActivityButton);
        etName = findViewById(R.id.etName);
        etCatName = findViewById(R.id.etCatName);
        addImage = findViewById(R.id.addIm);


        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent intent = result.getData();
                if (intent != null) {
                    if (result.getResultCode() == RESULT_OK ){
                        selectedImage = Uri.parse(String.valueOf(intent.getData()));
                        addImage.setImageURI(selectedImage);

                    }
                }
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                launcher.launch(intent);

            }
        });

        openActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedImage == null || TextUtils.isEmpty(etCatName.getText())|| TextUtils.isEmpty(etName.getText())){
                    Toast.makeText(MainActivity.this, "please fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    String imgSrc = selectedImage.toString();

                    Bitmap selectedImageBitmap = null;
                    try {
                        selectedImageBitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String imagePath = saveImageToInternalStorage(selectedImageBitmap, MainActivity.this);
                    try {
                        String ownername = etName.getText().toString();
                        String catname = etCatName.getText().toString();
                        dbManager.AddOwner(new OwnerDetails(ownername,catname,imagePath));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    // Create an Intent to open the new activity
                    Intent intent = new Intent(MainActivity.this, MainScreen.class);
                    startActivity(intent); // Start the new activity
                }
            }
        });
    }

    private String saveImageToInternalStorage(Bitmap bitmap, Context context) {
        try {
            // Use the app's private directory.

            String add =System.currentTimeMillis()+"";
            File directory = context.getDir("imageDir", Context.MODE_PRIVATE);
            // Name the file.
            File myImageFile = new File(directory, "selectedImage_"+add+".jpg");

            FileOutputStream fos = new FileOutputStream(myImageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            return myImageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    private void animateCat() {
        Glide.with(this).asGif().load(R.drawable.cat0).into(catImageView);
    }

}