package com.example.yourdoctordemo3.SleepManager;

import android.util.Log;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.SessionsClient;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResponse;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


/*
A class dedicated to work with Sleep Session Data as defined from googleApi.
Implemented for research reasons NOT USED as we read our data from our FireStore DB
 */
public class ReadSleepSessionsHandler {

    private static final String errorReadingTag = "Error Reading: ";
    private static final String totalSleepTag = "Total Sleep: ";
    private static final String sleepStageTag = "Sleep Stage: ";

    public static void readSleepSessions() throws ParseException {

        SessionsClient client = SleepRecordingHelper.getSessionsClient();

        SessionReadRequest sessionReadRequest = new SessionReadRequest.Builder()
                .read(DataType.TYPE_SLEEP_SEGMENT)
                .includeSleepSessions()
                .readSessionsFromAllApps()
                .setTimeInterval(SleepRecordingHelper.calculateMillisFromStringDate(SleepRecordingHelper.getPERIOD_START_DATE_TIME()),
                        SleepRecordingHelper.calculateMillisFromStringDate(SleepRecordingHelper.getPERIOD_END_DATE_TIME()),
                        TimeUnit.MILLISECONDS)
                .build();

        client.readSession(sessionReadRequest)
                .addOnSuccessListener(response -> Log.i("Response: ",response.getSessions().toString()))
                .addOnFailureListener(listener -> Log.e(errorReadingTag, "Unable to read sleep sessions", listener));
    }

    /**
     *
     * @param response Takes a SessionReadResponse which contains all the sleeping info of the week
     *
     * @implNote Calls dumpSleepSession method to further analyze sleep information per day
     */
    private static void dumpSleepSessions(SessionReadResponse response) {
        for (Session session : response.getSessions()) {
            dumpSleepSession(session, response.getDataSet(session));
        }
    }

    /**
     *
     * @param session Gets a session corresponding to a day's recording of sleep
     *
     * @param dataSets The related dataSets of this Session
     *
     * @implNote Calls subsequent methods: dumpSleepSessionMetadata and dumplSleepDataSets for further analysis
     */
    private static void dumpSleepSession(Session session, List<DataSet> dataSets) {
        dumpSleepSessionMetadata(session);
        dumpSleepDataSets(dataSets);
    }

    /**
     *
     * @param session This represents a day session input for sleep storing
     *
     * @implNote Gets from session the duration of all the sleep procedure and logs it in milliseconds
     */
    private static void dumpSleepSessionMetadata(Session session) {
        HashMap<String, String> sessionStartEnd = SleepRecordingHelper.getSessionStartAndEnd(session);

        long totalSleepForNight = TimeUnit.MILLISECONDS.toMinutes(calculateSessionDuration(
                session.getStartTime(TimeUnit.MILLISECONDS), session.getEndTime(TimeUnit.MILLISECONDS)));

        sessionStartEnd.entrySet().iterator().forEachRemaining(set -> {
            Log.i(totalSleepTag, set.getKey() + " to " + set.getValue() + "(" + totalSleepForNight + " min)");
        });
    }

    private static void dumpSleepDataSets(List<DataSet> dataSets) {
        dataSets.iterator().forEachRemaining(dataSet -> {
            dataSet.getDataPoints()
                    .forEach(dataPoint -> Log.i(sleepStageTag,
                            calculateSessionDuration(dataPoint.getStartTime(TimeUnit.MILLISECONDS),
                                    dataPoint.getEndTime(TimeUnit.MILLISECONDS)) + "(min)"));
        });

    }

    private static long calculateSessionDuration(long startTime, long endTime) {
        return TimeUnit.MILLISECONDS.toMinutes(endTime - startTime);
    }
}
