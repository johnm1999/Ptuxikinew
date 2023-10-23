package com.example.yourdoctordemo3.SleepManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import com.example.yourdoctordemo3.Common.AbstractMeasurementHelper;
import com.example.yourdoctordemo3.Common.GoogleSignInHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.SessionsClient;
import com.google.android.gms.fitness.data.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SleepRecordingHelper extends AbstractMeasurementHelper {

    @SuppressLint("StaticFieldLeak")
    @Setter
    @Getter
    private static SessionsClient sessionsClient;
    @Getter
    private static final String PERIOD_START_DATE_TIME = "10-10-2022T12:00:00Z";
    @Getter
    private static final String PERIOD_END_DATE_TIME = "16-10-2022T12:00:00Z";
    private static final String sleepStreamName = "Sleep_Stream";
    private static DataSource dataSource;
    private static final int SIGN_IN_REQUEST_CODE = 1001;
    @Getter
    private static GoogleSignInAccount account;

    private static final FitnessOptions fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
            .build();

    public static void setUpGoogleAccount(Context context) {
        account = GoogleSignInHelper.getGoogleAccount(context, fitnessOptions);
    }

    public static void sleepRecordingSetUp(Activity activity) throws ParseException, JSONException {
        if (oAuthPermissionsApproved()) {
            setContextForDataSource(activity.getApplicationContext());
            WriteSleepSessionsHandler.insertSleepSessions();
        } else {
            GoogleSignIn.requestPermissions(activity, SIGN_IN_REQUEST_CODE,
                    account, fitnessOptions);
        }
    }

    /**
     * @param context creates a new dataSource based on the given context
     */
    public static void setContextForDataSource(Context context) {
        dataSource = new DataSource.Builder()
                .setType(DataSource.TYPE_RAW)
                .setDataType(DataType.TYPE_SLEEP_SEGMENT)
                .setAppPackageName(context)
                .setStreamName(sleepStreamName)
                .build();
    }

    /**
     * @param session Takes an Object of type Session to read from
     * @return Returns the start time and the end time of a given Session object inside a HashMap<startTime, endTime>
     */
    protected static HashMap<String, String> getSessionStartAndEnd(Session session) {
        HashMap<String, String> startAndEnd = new HashMap<>();
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String startTime = dateFormat.format(session.getStartTime(TimeUnit.MILLISECONDS));
        String endTime = dateFormat.format(session.getEndTime(TimeUnit.MILLISECONDS));
        startAndEnd.put(startTime, endTime);
        return startAndEnd;
    }

    /**
     * @param dateTime     String value of the starting time for the segment. Will be translated into milliseconds
     * @param sleepPeriods JSONObject used to create DataPoints inside the DataSet
     * @return DataSet with DataPoints inside
     * @throws ParseException
     */
    protected static DataSet createDataSet(String dateTime, JSONObject sleepPeriods) throws ParseException, JSONException {
        DataPoint dataPoint;
        DataSet.Builder dataSet = DataSet.builder(dataSource);
        long startInMillis = calculateMillisFromStringDate(dateTime);

        for (int i = 0; i < sleepPeriods.length(); i++) {
            long sleepDuration = (long) sleepPeriods.get(Objects.requireNonNull(sleepPeriods.names()).getString(i));
            long sleepDurationInMillis = TimeUnit.MINUTES.toMillis(sleepDuration);
            dataPoint = DataPoint.builder(dataSource)
                    .setField(Field.FIELD_SLEEP_SEGMENT_TYPE, Integer.parseInt(Objects.requireNonNull(sleepPeriods.names()).getString(i)))
                    .setTimeInterval(startInMillis, startInMillis + sleepDurationInMillis, TimeUnit.MILLISECONDS)
                    .build();
            dataSet.add(dataPoint);
            startInMillis += sleepDurationInMillis;
        }


        return dataSet.build();
    }

    /**
     * @param dateTime String input of a DateTime representation with format: dd-MM-yyy'T'HH:mm:ss'Z'
     * @return returns a long value of time in milliseconds calculated from dateTime input
     * @throws ParseException
     */
    protected static long calculateMillisFromStringDate(String dateTime) throws ParseException {
        long timeInMillis = 0;
        if (StringUtils.isNotEmpty(dateTime)) {
            timeInMillis = Objects.requireNonNull(new SimpleDateFormat("dd-MM-yyy'T'HH:mm:ss'Z'", Locale.ENGLISH)
                            .parse(dateTime))
                    .getTime();
        }
        return timeInMillis;
    }

    private static boolean oAuthPermissionsApproved() {
        return GoogleSignIn.hasPermissions(account, fitnessOptions);
    }

    @Override
    protected void getSensorData(DataType dataType) {
    }
}
