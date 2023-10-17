package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Random;

public class ALodingDialog extends Dialog {
    TextView textView;
    public ALodingDialog(@NonNull Context context) {
        super(context);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.loding_layout,null);

        textView = view.findViewById(R.id.phrase);
        setContentView(view);
    }

    public void setmyText()
    {
        String[] phrases = {
                "Dispensing happiness for your fur baby! Enjoy the show!",
                "Food's on its way! Your cat's taste buds are about to have a party!",
                "Prepare for liftoff! Cat food incoming in 3, 2, 1...",
                "Hold on to your whiskers! Food delivery in progress!",
                "Meow's the time! Your cat's meal is zooming its way.",
                "Cat food launching soon! Get ready for a purrfect mealtime.",
                "Cat-alicious delivery on the way! Brace yourselves, feline foodies!",
                "Incoming culinary delight for your cat! Bon appétit, furball!",
                "Cat cuisine inbound! Your cat's taste adventure begins now.",
                "Hold your catnip, it's mealtime magic in action!"
        };

        Random random = new Random();
        int randomIndex = random.nextInt(phrases.length);
        textView.setText(phrases[randomIndex]);



    }

}
