package com.example.yourdoctordemo3.Common;

import android.util.Log;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.RecordingClient;
import com.google.android.gms.fitness.SensorsClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.support.wearable.view.FullscreenFragmentHelper.TAG;

public abstract class AbstractMeasurementHelper {

    @Setter
    @Getter
    private RecordingClient recordingClient;

    @Setter
    @Getter
    private SensorsClient sensorsClient;

    @Getter
    private final List<Float> data = new ArrayList<>();

    public void subscribeMeasurement(DataType dataType) {
        this.recordingClient.subscribe(dataType)
                .addOnSuccessListener(unused -> Log.i(TAG, dataType.toString()))
                .addOnFailureListener(e -> Log.e(TAG, "There was an error subscribing: ", e));

        getSensorData(dataType);
    }

    public void unsubscribeMeasurement(DataType dataType) {
        if(this.recordingClient.listSubscriptions(dataType).isSuccessful()){
            recordingClient.unsubscribe(dataType)
                    .addOnSuccessListener(unused ->
                            Log.i(TAG, "Successfully unsubscribed."))
                    .addOnFailureListener(e -> Log.w(TAG, "Failed to unsubscribe."));
        }
    }

    protected abstract void getSensorData(DataType dataType);
}
