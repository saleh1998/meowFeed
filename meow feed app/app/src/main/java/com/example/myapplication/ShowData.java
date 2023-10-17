package com.example.myapplication;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ShowData extends AppCompatActivity {
    DB_Manager dbManager = DB_Manager.getInstance(this);
    BarChart barChart;
    ImageButton btnBack;
    Spinner monthSpinner, yearSpinner;
    int selectedYear = 2023; // Default year
    int selectedMonth = 10; // Default month (0-based, January is 0)
    TextView totalWeightTextView,averageTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        barChart = findViewById(R.id.barChart);
        btnBack = findViewById(R.id.ShowData_btnBack);
        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        totalWeightTextView = findViewById(R.id.totalWeightTextView);
        averageTextView = findViewById(R.id.averageTextView);

      /*  try {
            dbManager.addSampleDataForMonth(2023, 4);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }*/

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedMonthString = parentView.getItemAtPosition(position).toString();
                selectedMonth = getMonthNumber(selectedMonthString);
                updateChart(); // Update the chart when the month is selected
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle no selection
            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedYear = Integer.parseInt(parentView.getItemAtPosition(position).toString());
                updateChart(); // Update the chart when the year is selected
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle no selection
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateChart() {
        try {
            HashMap<String, Double> monthlyConsumptionMap = calculateMonthlyConsumption(selectedYear, selectedMonth);
            ArrayList<BarEntry> entries = new ArrayList<>();

            double totalWeight = 0;
            double totalEaten = 0;

            int index = 0;
            for (Map.Entry<String, Double> entry : monthlyConsumptionMap.entrySet()) {
                entries.add(new BarEntry(index++, entry.getValue().floatValue()));
                totalEaten += entry.getValue();
                totalWeight += entry.getValue(); // Assuming entry.getValue() represents the total weight for the day
            }

            // Calculate average
            double average = totalEaten / monthlyConsumptionMap.size();


            // Inside your updateChart() method, after calculating totalWeight and average
            totalWeightTextView.setText("TOTAL: " + String.format("%.2f", totalWeight) +" g");
            averageTextView.setText("AVG: " + String.format("%.2f", average)+" g");


            BarDataSet dataSet = new BarDataSet(entries, "Cat's Daily Consumption");
            BarData data = new BarData(dataSet);
            barChart.setData(data);

            // Customize the appearance of the chart as needed
            barChart.getDescription().setEnabled(false);
            barChart.setDrawGridBackground(false);
            barChart.setDrawBarShadow(false);
            barChart.setDrawValueAboveBar(true);
            barChart.getXAxis().setGranularity(1f);
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(monthlyConsumptionMap.keySet().toArray(new String[0])));

            barChart.invalidate(); // Refresh the chart

        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the exception, possibly show an error message to the user
        }
    }

    private int getMonthNumber(String monthName) {
        String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        for (int i = 0; i < months.length; i++) {
            if (months[i].equalsIgnoreCase(monthName)) {
                return i;
            }
        }
        return 0; // Default to January if month name is not recognized
    }

    private HashMap<String, Double> calculateMonthlyConsumption(int year, int month) throws ParseException {
        ArrayList<CatData> monthlyData = getDataForMonth(year, month);
        HashMap<String, Double> monthlyConsumptionMap = new HashMap<>();

        for (CatData data : monthlyData) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateKey = dateFormat.format(data.getDate());

            if (monthlyConsumptionMap.containsKey(dateKey)) {
                double totalEaten = monthlyConsumptionMap.get(dateKey) + data.getEaten();
                monthlyConsumptionMap.put(dateKey, totalEaten);
            } else {
                monthlyConsumptionMap.put(dateKey, data.getEaten());
            }
        }

        return monthlyConsumptionMap;
    }

    private ArrayList<CatData> getDataForMonth(int year, int month) throws ParseException {
        ArrayList<CatData> allMealsData = dbManager.getAllMealsData();
        ArrayList<CatData> monthlyData = new ArrayList<>();

        for (CatData data : allMealsData) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(data.getDate());
            int dataYear = calendar.get(Calendar.YEAR);
            int dataMonth = calendar.get(Calendar.MONTH);

            if (dataYear == year && dataMonth == month) {
                monthlyData.add(data);
            }
        }

        return monthlyData;
    }

}
