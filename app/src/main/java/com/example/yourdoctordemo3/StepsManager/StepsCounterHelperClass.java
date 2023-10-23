package com.example.yourdoctordemo3.StepsManager;

import com.example.yourdoctordemo3.Common.AbstractMeasurementHelper;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.SensorRequest;

import java.util.concurrent.TimeUnit;

public class StepsCounterHelperClass extends AbstractMeasurementHelper {
    @Override
    protected void getSensorData(DataType dataType) {
        this.getData().clear();
        super.getSensorsClient().add(
                new SensorRequest.Builder()
                        .setDataType(dataType)
                        .setSamplingRate(1, TimeUnit.MILLISECONDS)
                        .build(),
                dataPoint -> {
                    float data = Float.parseFloat(dataPoint.getValue(Field.FIELD_STEPS).toString());
                    if (data < 250) {
                        this.getData().add(data);
                    }
                }
        );
    }
}
