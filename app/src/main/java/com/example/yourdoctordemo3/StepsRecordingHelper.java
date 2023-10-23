package com.example.yourdoctordemo3;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.example.yourdoctordemo3.Common.GoogleSignInHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import java.util.concurrent.atomic.AtomicInteger;

import static com.google.android.gms.fitness.data.Field.FIELD_STEPS;



public class StepsRecordingHelper {

    private static final int SIGN_IN_REQUEST_CODE = 1001;
    private static final String TAG = "DAILY STEPS: ";
    private static GoogleSignInAccount account;



    private static final FitnessOptions fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .build();

    static void fitSignIn(Activity activity) {
        account = GoogleSignInHelper.getGoogleAccount(activity.getApplicationContext(), fitnessOptions);
        if (oAuthPermissionsApproved(activity)) {
            readDailySteps(activity.getApplicationContext());
        } else {
            GoogleSignIn.requestPermissions(activity, SIGN_IN_REQUEST_CODE,
                    account, fitnessOptions);
        }
    }

    static void readDailySteps(Context context) {
        AtomicInteger total = new AtomicInteger();
        Fitness.getHistoryClient(context, GoogleSignInHelper.getGoogleAccount(context, fitnessOptions))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(dataSet -> {
                    if (!dataSet.isEmpty())
                        total.set(Integer.parseInt(dataSet.getDataPoints()
                                .get(0).getValue(FIELD_STEPS).toString()));
                    Log.i(TAG, "Total steps: " + total);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "There was a problem getting the step count.", e);
                });
    }

    private static boolean oAuthPermissionsApproved(Activity activity) {
        return GoogleSignIn.hasPermissions(account, fitnessOptions);
    }
}
