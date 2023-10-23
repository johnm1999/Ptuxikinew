package com.example.yourdoctordemo3.SleepManager;

import com.example.yourdoctordemo3.Common.FireStoreHelper;
import com.example.yourdoctordemo3.Common.GoogleSignInHelper;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.*;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import org.json.JSONException;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WriteSleepSessionsHandler {

    private static final String sessionDescription = "This session is getting hardcoded inputs";
    private static final String sessionName = "Sleep";
    private static final String sessionIdentifier = "Here you can enter an identifier for each different session";

    public static void insertSleepSessions() throws ParseException, JSONException {
        List<SessionInsertRequest> sleepSessionsRequests = createSleepSessionsRequests();

        //Code to be used in case of interaction with GoogleFit Api DB(Remove if you want)

//        if (CollectionUtils.isNotEmpty(sleepSessionsRequests)) {
//            sleepSessionsRequests.iterator().forEachRemaining(sessionRequest -> {
//                HashMap<String, String> sessionStartEnd = getSessionStartAndEnd(sessionRequest.getSession());
//                for (Map.Entry<String, String> entry : sessionStartEnd.entrySet()) {
//                    SleepRecordingHelper.getSessionsClient().insertSession(sessionRequest)
//                            .addOnSuccessListener(listener ->
//                                    Log.i("Session_Info",
//                                            "Start: " + entry.getKey()
//                                                    + " End: " + entry.getValue()))
//                            .addOnFailureListener(failure -> Log.e("Error",
//                                    "Failed to insert session for: " + sessionRequest));
//                }
//            });
//        }
    }

    /**
     * Inserting Data into FireStore while also
     * creating the Sessions to be sent in GoogleFit Api in case we want to store the info there.
     *
     * @return Returns List<SessionInsertRequest> to be used inside Fitness API
     * @throws ParseException
     */
    private static List<SessionInsertRequest> createSleepSessionsRequests() throws ParseException, JSONException {
        List<SessionInsertRequest> sessionInsertRequests = new ArrayList<>();
        List<DataSet> dataSets = SleepTestingInputs.createSleepDataSets();
        HashMap<String, String> sameDaySleepData = new HashMap<>();
        List<String> dataToBeInserted = new ArrayList<>();

        dataSets.iterator().forEachRemaining(dataSet -> {
            populateSleepData(dataSet, sameDaySleepData);
            try {
                Session session = createSleepSession(dataSet);

                sessionInsertRequests.add(new SessionInsertRequest.Builder()
                        .addDataSet(dataSet)
                        .setSession(session)
                        .build()
                );
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });


        sameDaySleepData.keySet()
                .stream()
                .sorted()
                .forEach(key -> dataToBeInserted.add(key + " " + sameDaySleepData.get(key)));

        FireStoreHelper.insertData("SleepData", "SleepRecord",  dataToBeInserted, "ID");
        return sessionInsertRequests;
    }

    /**
     * @param dataSet DataSet object to provide info for start - end time per Data Point included
     * @return Creates a Session for Sleep Activities given the duration of it based on the DataSet input
     * @throws ParseException
     */
    private static Session createSleepSession(DataSet dataSet) throws ParseException {
        return new Session.Builder()
                .setName(sessionName)
                .setIdentifier(sessionIdentifier)
                .setDescription(sessionDescription)
                .setStartTime(dataSet.getDataPoints().get(0).getStartTime(TimeUnit.MILLISECONDS),
                        TimeUnit.MILLISECONDS) // From first segment
                .setEndTime(dataSet.getDataPoints().get(dataSet.getDataPoints().size() - 1).getEndTime(TimeUnit.MILLISECONDS),
                        TimeUnit.MILLISECONDS) // From last segment
                .setActivity(FitnessActivities.SLEEP)
                .build();
    }

    /**
     *
     * @param dataSet The dataSet from which the info will be read.
     *
     * @param sameDaySleepData A HashMap to insert the information read from the dataSet with key the date and
     *                         value a String representation of type of sleep with the duration of it.
     */
    private static void populateSleepData(DataSet dataSet, HashMap<String, String> sameDaySleepData) {
        dataSet.getDataPoints().iterator().forEachRemaining(dataPoint -> {
            String info = new Date(dataPoint.getStartTime(TimeUnit.MILLISECONDS)).toString().substring(4, 16);
            sameDaySleepData.put(info, buildDataString(dataPoint));
        });
    }

    //Builds a string representation of the data to be inserted as value in FireStore
    private static String buildDataString(DataPoint dataPoint) {
        return getSleepType(dataPoint.getValue(Field.FIELD_SLEEP_SEGMENT_TYPE).asInt()) + " " +
                (dataPoint.getEndTime(TimeUnit.MINUTES) - dataPoint.getStartTime(TimeUnit.MINUTES)) + " min";
    }

    //Decoding the int to State of Sleep
    private static String getSleepType(int intType) {
        String type;

        switch (intType) {
            case (1):
                type = "AWAKE";
                break;
            case (2):
                type = "SLEEP";
                break;
            case (3):
                type = "OUT_OF_BED";
                break;
            case (4):
                type = "SLEEP_LIGHT";
                break;
            case (5):
                type = "SLEEP_DEEP";
                break;
            case (6):
                type = "SLEEP_REM";
                break;
            default:
                type = " ";
        }
        return type;
    }
}
