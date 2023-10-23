package com.example.yourdoctordemo3.HeartRateManager;

import com.example.yourdoctordemo3.Common.AbstractMeasurementHelper;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.SensorRequest;

import java.util.concurrent.TimeUnit;

public class HeartRateHelperClass extends AbstractMeasurementHelper {
    public HeartRateHelperClass() {
    }

    @Override
    protected void getSensorData(DataType dataType) {
        this.getData().clear();
        super.getSensorsClient().add(
                new SensorRequest.Builder()
                        .setDataType(dataType)
                        .setSamplingRate(1, TimeUnit.SECONDS)
                        .build(),
                dataPoint -> {
                    float data = Float.parseFloat(dataPoint.getValue(Field.FIELD_BPM).toString());
                    if (data < 250) {
                        this.getData().add(data);
                    }
                }
        );
    }
}
