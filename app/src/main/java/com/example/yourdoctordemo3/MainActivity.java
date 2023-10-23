package com.example.yourdoctordemo3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import com.example.yourdoctordemo3.HeartRateManager.Heart_Rate;
import com.example.yourdoctordemo3.SleepManager.Sleep;
import com.example.yourdoctordemo3.SleepManager.SleepRecordingHelper;
import com.example.yourdoctordemo3.SleepManager.WriteSleepSessionsHandler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.tasks.Task;
import org.json.JSONException;

import java.text.ParseException;

public class MainActivity extends WearableActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BODY_SENSORS}, 1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 2);


        SleepRecordingHelper.setUpGoogleAccount(this);
        SleepRecordingHelper.setSessionsClient(Fitness.getSessionsClient(this, GoogleSignInAccount.createDefault()));


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SleepRecordingHelper.sleepRecordingSetUp(MainActivity.this);
                } catch (ParseException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        ImageView heartbtn = findViewById(R.id.heart_rate);
        ImageView stepsbtn = findViewById(R.id.steps);
        ImageView sleeping = findViewById(R.id.sleeping);

        heartbtn.setOnClickListener(view -> openHeartActivity());
        stepsbtn.setOnClickListener(view -> openStepsActivity());
        sleeping.setOnClickListener(view -> openSleepActivity());


        // Enables Always-on
        setAmbientEnabled();
    }

    public void openHeartActivity() {
        Intent intent = new Intent(this, Heart_Rate.class);
        startActivity(intent);
    }

    public void openStepsActivity() {
        Intent intent = new Intent(this, Steps.class);
        startActivity(intent);
    }


    public void openSleepActivity() {
        Intent intent = new Intent(this, Sleep.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResutls) {
        //Exit app if not given permission to read sensors
        if (requestCode == 1) {
            if (grantResutls.length == 0 || grantResutls[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                onStop();
                onDestroy();
            }
        }
        if (requestCode == 2) {
            if (grantResutls.length == 0 || grantResutls[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                onStop();
                onDestroy();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        //Here needs to be implemented the OAuth client of google with the corresponding ID.
        // We skip this part for testing reasons(Also we don't care about GoogleFit DB as we save our data in FireStore
        if (requestCode == 1001) {
            try {
                SleepRecordingHelper.setContextForDataSource(MainActivity.this);
                WriteSleepSessionsHandler.insertSleepSessions();
            } catch (ParseException | JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.i("SignIn: ", "Sign In Successful");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SignInError", "signInResult:failed code=" + e.getStatusCode());
            onStop();
            onDestroy();
        }
    }
}