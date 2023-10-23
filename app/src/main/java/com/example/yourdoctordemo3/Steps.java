package com.example.yourdoctordemo3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.yourdoctordemo3.Common.FireStoreHelper;
import com.example.yourdoctordemo3.Common.GoogleSignInHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Steps extends WearableActivity implements SensorEventListener {

    private static final String TAG = "Steps";
    private TextView steps;
    private SensorManager mSensorManager;
    private Sensor stepsensor;
    private TextView distance;
    private TextView status;
    private TextView status2;
    private boolean isCounterSensorPresent;
    private String date;
    int stepcount;
    private int clearWeek = 0;

    private TextView weeklystep;
    private TextView dayofhistory;

    private String flagDate;
    private String daysteps;

    SharedPreferences sp;

    List<String> dailySteps = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);



        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleformat = new SimpleDateFormat("dd/MMMM/yyyy hh:mm:s");
        SimpleDateFormat f = new SimpleDateFormat(" EEEE");
        date = f.format(new Date());




        steps = (TextView) findViewById(R.id.stepscount);
        distance = (TextView) findViewById(R.id.distance);
        status = (TextView) findViewById(R.id.status);
        status2 = (TextView) findViewById(R.id.status2);
        weeklystep = (TextView) findViewById(R.id.WeeklySteps);
        dayofhistory = (TextView) findViewById(R.id.todays_steps);


        //Show Todays steps
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("email")
                .document("Steps")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            weeklystep.setText(doc.getString("Today0"));
                            Log.w(TAG, " getting documents.", task.getException());
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        //Enables Always-on
        setAmbientEnabled();

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            stepsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        }
        else{
            steps.setText("Counter Sensor is not Present");
            isCounterSensorPresent = false;
        }

        if(sp != null && sp.getString("date", null) != null && !sp.getString("date", null).equals(date)) {
            dailySteps.clear();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        sp = getSharedPreferences("StepsData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        stepcount = sp.getInt("steps",stepcount);



        if(sensorEvent.sensor == stepsensor){
            stepcount = (int) sensorEvent.values[0];
            steps.setText(String.valueOf(stepcount));
            status2.setText(date);
            flagDate = date;
            daysteps = date.concat(String.valueOf(stepcount));
            dayofhistory.setText(date + ':');
            editor.putInt("steps",stepcount);
            editor.putString("date",date);
            editor.apply();
            if(sp != null && sp.getString("date", null) != null && !sp.getString("date", null).equals(date)){//gia na einai evdomadad mporo na valw metriti timer  na kanei clear to layout
                //create history
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.History);
                TextView textView = new TextView(Steps.this);
                textView.setText(date + ":"+ stepcount);
                textView.setTextSize(15);
                textView.setTextColor(Color.parseColor("#bdbdbd"));
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setBackground(ContextCompat.getDrawable(Steps.this,R.drawable.gardient_list));
                linearLayout.addView(textView);

            }

            dailySteps.add(""+stepcount);
            FireStoreHelper.insertData("Steps", "Today", dailySteps, "ID");

        }
        if(stepcount<3000){
            status.setText("Start walking");
        }
        else if(stepcount>3000 && stepcount<6000){
            status.setText("Healthy");
        }
        else if(stepcount<9000){
            status.setText("athlete");
        }
        else{
            status.setText("Champion");
        }
        float flagdistance = (float) (stepcount*78)/(float)100000; //metatropi steps to km
        distance.setText(new DecimalFormat("###.##").format(flagdistance));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onResume(){
        super.onResume();
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null)
        {
            mSensorManager.registerListener(this,stepsensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            mSensorManager.unregisterListener(this,stepsensor);
        }


    }
}