package com.example.yourdoctordemo3.HeartRateManager;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.yourdoctordemo3.Common.FireStoreHelper;
import com.example.yourdoctordemo3.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class Heart_Rate extends WearableActivity {

    private TextView heartText;
    private TextView historyText;
    private static final HeartRateHelperClass heartRateHelperClass = new HeartRateHelperClass();
    private static final String WARNING_TAG = "WARNING";
    private static final String EMPTY_HEART_DATA = "Heart Rate data are empty!";
    private static final String WRONG_BUTTON = "Heart rate is not running!";
    private String latestAvgValue;
    private static List<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart__rate);

        heartText = findViewById(R.id.heart_rate_text);
        historyText = findViewById(R.id.heartHistory);
        ImageButton startRate = findViewById(R.id.start_heart_rate_btn);
        ImageButton stopRate = findViewById(R.id.stop_heart_rate_btn);


        heartRateHelperClass.setRecordingClient(Fitness.getRecordingClient(this,
                GoogleSignInAccount.createDefault()));

        heartRateHelperClass.setSensorsClient(Fitness.getSensorsClient(this,
                GoogleSignInAccount.createDefault()));

        //Subscribe listener and update text field
        startRate.setOnClickListener(view -> {
            heartText.setText(R.string.startHeartRateMeasure);

            heartText.setTextColor(Color.WHITE);

            /*FitnessOptions.builder()
                    .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_WRITE)
                    .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY,FitnessOptions.ACCESS_WRITE)
                    .build();*/
            startMeasure();

            //DataReadRequest data = queryFitnessData2();
        });

        //Unsubscribe listener and store/print heartRateData
        stopRate.setOnClickListener(view -> stopMeasure());

        setAmbientEnabled();
    }

    private DataReadRequest queryFitnessData2() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date now = new Date();

        calendar.setTime(now);

        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        return new DataReadRequest.Builder()
                .enableServerQueries()
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .build();
    }


    private void startMeasure() {
        heartRateHelperClass.subscribeMeasurement(DataType.TYPE_HEART_RATE_BPM);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void stopMeasure() {
        heartRateHelperClass.unsubscribeMeasurement(DataType.TYPE_HEART_RATE_BPM);
        if(CollectionUtils.isEmpty(heartRateHelperClass.getData()) ||
                heartText.getText().equals(latestAvgValue) || heartText.getText().equals(WRONG_BUTTON)){
            Log.w(WARNING_TAG, EMPTY_HEART_DATA);
            heartText.setText(WRONG_BUTTON);
            heartText.setTextColor(Color.RED);
        } else {
            Log.i("HeartData", heartRateHelperClass.getData().toString());
            latestAvgValue = getAverageHeartRate();
            data.add(latestAvgValue);
            heartText.setText(latestAvgValue);

            //FireStore
            FireStoreHelper.insertData("HeartRate", "AvgRate",  data, "Johny");
            updateHeartRateData();
        }
    }

    public void updateHeartRateData() {
        historyText.setText("");
        data.forEach(str -> {
            historyText.append(str + '\n');
        });
    }

    @SuppressLint("DefaultLocale")
    private static String getAverageHeartRate() {
        Float aggregatedData = 0.f;
        List<Float> heartRateHelperClassData = heartRateHelperClass.getData();

        if (CollectionUtils.isNotEmpty(heartRateHelperClassData)) {
            for (Float data : heartRateHelperClassData) {
                aggregatedData += data;
            }
        }

        if (aggregatedData != 0) {
            return String.format("%3.2f", aggregatedData / heartRateHelperClassData.size());
        }
        return null;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(StringUtils.isEmpty(historyText.getText())){
            updateHeartRateData();
        }
    }
}