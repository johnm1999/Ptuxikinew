package com.example.yourdoctordemo3.SleepManager;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.yourdoctordemo3.Common.FireStoreHelper;
import com.example.yourdoctordemo3.R;
import com.example.yourdoctordemo3.SleepManager.ReadSleepSessionsHandler;
import com.google.android.gms.fitness.data.DataType;

import java.text.ParseException;

public class Sleep extends WearableActivity {

    private TextView sleepData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);


        sleepData =  findViewById(R.id.sleepData);
        FireStoreHelper.readData(sleepData, DataType.TYPE_SLEEP_SEGMENT);

        // Enables Always-on
        setAmbientEnabled();
    }



}