package com.example.myapplication;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.lang.reflect.Field;
import java.util.Calendar;

public class ScheduleFragment extends Fragment {

    Context context;
    DatePicker datePicker;
    TimePicker timePicker;
    Button scheduleButton;
    View fragView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragView = inflater.inflate(R.layout.fragment_schedule, container, false);
        datePicker = fragView.findViewById(R.id.datePicker);
        timePicker = fragView.findViewById(R.id.timePicker);
        scheduleButton = fragView.findViewById(R.id.scheduleButton);

        // Initially, hide the time picker

        timePicker.setVisibility(View.INVISIBLE);
        scheduleButton.setVisibility(View.INVISIBLE);

        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Show the time picker when the user selects a date
                        // Set the TimePicker mode to keyboard mode
                        datePicker.setVisibility(View.INVISIBLE);
                        timePicker.setVisibility(View.VISIBLE);
                        scheduleButton.setVisibility(View.VISIBLE);


                        // You can also add additional logic here if needed
                    }
                });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user-selected date and time
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int dayOfMonth = datePicker.getDayOfMonth();
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Schedule the feeding job based on user input
                scheduleFeedingJob(year, month, dayOfMonth, hour, minute);
            }
        });

        return fragView;
    }


    private void scheduleFeedingJob(int year, int month, int dayOfMonth, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        long scheduledTimeMillis = calendar.getTimeInMillis();
        long currentTimeMillis = System.currentTimeMillis();

        // Check if the scheduled time is in the future
        if (scheduledTimeMillis > currentTimeMillis) {
            long delayMillis = scheduledTimeMillis - currentTimeMillis;

            ComponentName componentName = new ComponentName(requireContext(), FeedingJobService.class);
            JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                    .setMinimumLatency(delayMillis)
                    .setPersisted(true) // Persist the job across device reboots
                    .build();

            JobScheduler jobScheduler = (JobScheduler) requireContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);

            // Notify the user that the feeding is scheduled
            String scheduledTime = String.format("%02d:%02d %02d/%02d/%04d", hour, minute, dayOfMonth, month + 1, year);
            String message = "Feeding scheduled for: " + scheduledTime;
            showToast(message);
        } else {
            // Notify the user that the selected time is in the past
            showToast("Invalid time selection. Please select a future time.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    public void setContext(Context context) {
        this.context = context;
    }


}


